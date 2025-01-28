package com.unimib.ignitionfinance.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.unimib.ignitionfinance.data.calculator.DailyReturnCalculator
import com.unimib.ignitionfinance.data.model.user.DailyReturn
import com.unimib.ignitionfinance.domain.validation.DatasetValidationResult
import com.unimib.ignitionfinance.domain.validation.DatasetValidator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class CalculateDailyReturnsUseCase @Inject constructor(
    private val fetchHistoricalDataUseCase: FetchHistoricalDataUseCase,
    private val getProductListUseCase: GetProductListUseCase,
    private val dailyReturnCalculator: DailyReturnCalculator
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun execute(apiKey: String): Flow<Result<List<DailyReturn>>> = flow {
        try {
            // Step 1: Get product list
            val productsResult = getProductListUseCase.execute().first()
            val products = productsResult.getOrElse {
                emit(Result.failure(it))
                return@flow
            }

            // Step 2: Fetch historical data
            val historicalDataResult = fetchHistoricalDataUseCase.execute(apiKey).first()
            val historicalDataList = historicalDataResult.getOrElse {
                emit(Result.failure(it))
                return@flow
            }

            // Step 3: Validate dataset
            when (val validationResult = DatasetValidator.validate(historicalDataList)) {
                is DatasetValidationResult.Failure -> {
                    emit(Result.failure(Exception(validationResult.message)))
                    return@flow
                }
                is DatasetValidationResult.Success -> {
                    // Only execute remaining steps if validation succeeds
                    // Step 4: Extract tickers
                    val productTickers = products.map { it.ticker }

                    // Step 5: Prepare capital map
                    val productCapitals = products.associate { product ->
                        product.ticker to try {
                            BigDecimal(product.amount)
                        } catch (e: NumberFormatException) {
                            BigDecimal.ZERO
                        }
                    }

                    // Step 6: Calculate returns
                    val dailyReturns = dailyReturnCalculator.calculateDailyReturns(
                        historicalDataList = historicalDataList,
                        products = productTickers,
                        productCapitals = productCapitals
                    )

                    emit(Result.success(dailyReturns))
                }
            }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}