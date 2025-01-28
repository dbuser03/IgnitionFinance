package com.unimib.ignitionfinance.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.unimib.ignitionfinance.data.calculator.DailyReturnCalculator
import com.unimib.ignitionfinance.data.model.StockData
import com.unimib.ignitionfinance.data.model.user.DailyReturn
import com.unimib.ignitionfinance.data.model.user.Product
import com.unimib.ignitionfinance.data.repository.interfaces.StockRepository
import com.unimib.ignitionfinance.domain.validation.DatasetValidationResult
import com.unimib.ignitionfinance.domain.validation.DatasetValidator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class BuildDatasetUseCase @Inject constructor(
    private val fetchHistoricalDataUseCase: FetchHistoricalDataUseCase,
    private val getProductListUseCase: GetProductListUseCase,
    private val dailyReturnCalculator: DailyReturnCalculator,
    private val stockRepository: StockRepository,
    private val saveDatasetUseCase: SaveDatasetUseCase  // New injection
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

            // Step 3: Validate dataset and process
            val dailyReturns = when (val validationResult = DatasetValidator.validate(historicalDataList)) {
                is DatasetValidationResult.Success -> {
                    // Process data for valid dataset
                    processData(products, historicalDataList)
                }

                is DatasetValidationResult.Failure -> {
                    // Fallback to S&P 500
                    val sp500Result = stockRepository.fetchStockData("^GSPC", apiKey).first()
                    val sp500Data = sp500Result.getOrElse { error ->
                        emit(Result.failure(error))
                        return@flow
                    }

                    // Calculate total capital
                    val totalCapital = calculateTotalCapital(products)

                    // Create fallback dataset
                    val fallbackData = listOf(sp500Data)
                    val fallbackTickers = listOf("^GSPC")
                    val fallbackCapitals = mapOf("^GSPC" to totalCapital)

                    // Calculate returns for fallback
                    dailyReturnCalculator.calculateDailyReturns(
                        historicalDataList = fallbackData,
                        products = fallbackTickers,
                        productCapitals = fallbackCapitals
                    )
                }
            }

            // Step 4: Save the dataset (whether it's the full dataset or S&P 500 fallback)
            val saveResult = saveDatasetUseCase.execute(dailyReturns).first()
            saveResult.getOrElse { error ->
                emit(Result.failure(error))
                return@flow
            }

            // Step 5: Emit the final result
            emit(Result.success(dailyReturns))

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun calculateTotalCapital(products: List<Product>): BigDecimal {
        return products.fold(BigDecimal.ZERO) { acc, product ->
            acc + try {
                BigDecimal(product.amount)
            } catch (e: NumberFormatException) {
                BigDecimal.ZERO
            }
        }
    }

    private fun processData(
        products: List<Product>,
        historicalData: List<Map<String, StockData>>
    ): List<DailyReturn> {
        val productTickers = products.map { it.ticker }
        val productCapitals = products.associate { product ->
            product.ticker to try {
                BigDecimal(product.amount)
            } catch (e: NumberFormatException) {
                BigDecimal.ZERO
            }
        }

        return dailyReturnCalculator.calculateDailyReturns(
            historicalDataList = historicalData,
            products = productTickers,
            productCapitals = productCapitals
        )
    }
}