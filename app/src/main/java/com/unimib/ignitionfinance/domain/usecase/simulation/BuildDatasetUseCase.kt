package com.unimib.ignitionfinance.domain.usecase.simulation

import android.os.Build
import androidx.annotation.RequiresApi
import com.unimib.ignitionfinance.BuildConfig
import com.unimib.ignitionfinance.domain.utils.DailyReturnCalculator
import com.unimib.ignitionfinance.data.model.StockData
import com.unimib.ignitionfinance.data.model.user.DailyReturn
import com.unimib.ignitionfinance.data.model.user.Product
import com.unimib.ignitionfinance.data.repository.interfaces.StockRepository
import com.unimib.ignitionfinance.domain.usecase.FetchHistoricalDataUseCase
import com.unimib.ignitionfinance.domain.usecase.networth.invested.GetProductListUseCase
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
    private val saveDatasetUseCase: SaveDatasetUseCase
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun execute(apiKey: String): Flow<Result<List<DailyReturn>>> = flow {
        try {
            val productsResult = getProductListUseCase.execute(BuildConfig.ALPHAVANTAGE_API_KEY).first()
            val products = productsResult.getOrElse {
                emit(Result.failure(it))
                return@flow
            }

            val historicalDataResult = fetchHistoricalDataUseCase.execute(apiKey).first()
            val historicalDataList = historicalDataResult.getOrElse {
                emit(Result.failure(it))
                return@flow
            }

            val dailyReturns: List<DailyReturn> = when (DatasetValidator.validate(historicalDataList)) {
                is DatasetValidationResult.Success -> {
                    processData(products, historicalDataList)
                }
                is DatasetValidationResult.Failure -> {
                    android.util.Log.w("BuildDatasetUseCase", "Validazione fallita: il dataset non contiene dati validi. Procedo con i dati dello S&P500.")

                    val sp500Result = stockRepository.fetchStockData("GSPC", BuildConfig.ALPHAVANTAGE_API_KEY).first()
                    val sp500Data = sp500Result.getOrElse { error ->
                        emit(Result.failure(error))
                        return@flow
                    }

                    val totalCapital = calculateTotalCapital(products)

                    val fallbackData = listOf(sp500Data)
                    val fallbackTickers = listOf("GSPC")
                    val fallbackCapitals = mapOf("GSPC" to totalCapital)

                    dailyReturnCalculator.calculateDailyReturns(
                        historicalDataList = fallbackData,
                        products = fallbackTickers,
                        productCapitals = fallbackCapitals
                    )
                }

            }

            val saveResult = saveDatasetUseCase.execute(dailyReturns).first()
            saveResult.getOrElse { error ->
                emit(Result.failure(error))
                return@flow
            }

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
            } catch (_: NumberFormatException) {
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
            } catch (_: NumberFormatException) {
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