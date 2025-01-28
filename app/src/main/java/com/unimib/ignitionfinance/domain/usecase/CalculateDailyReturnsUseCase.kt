package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.calculator.DailyReturnCalculator
import com.unimib.ignitionfinance.data.model.user.DailyReturn
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
    fun execute(apiKey: String): Flow<Result<List<DailyReturn>>> = flow {
        try {
            // Step 1: Get the list of products
            val productsResult = getProductListUseCase.execute().first()

            val products = productsResult.getOrElse {
                emit(Result.failure(it))
                return@flow
            }

            // Step 2: Fetch historical data for all products
            val historicalDataResult = fetchHistoricalDataUseCase.execute(apiKey).first()

            val historicalDataList = historicalDataResult.getOrElse {
                emit(Result.failure(it))
                return@flow
            }

            // Step 3: Extract product tickers in the same order as historical data
            val productTickers = products.map { it.ticker }

            // Step 4: Create a map of product tickers to their invested amounts
            val productCapitals = products.associate { product ->
                product.ticker to try {
                    BigDecimal(product.amount)
                } catch (e: NumberFormatException) {
                    BigDecimal.ZERO
                }
            }

            // Step 5: Calculate daily returns using the calculator
            val dailyReturns = dailyReturnCalculator.calculateDailyReturns(
                historicalDataList = historicalDataList,
                products = productTickers,
                productCapitals = productCapitals
            )

            emit(Result.success(dailyReturns))

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}