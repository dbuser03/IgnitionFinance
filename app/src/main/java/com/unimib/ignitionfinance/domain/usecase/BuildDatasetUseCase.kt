package com.unimib.ignitionfinance.domain.usecase

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.unimib.ignitionfinance.BuildConfig
import com.unimib.ignitionfinance.data.calculator.DailyReturnCalculator
import com.unimib.ignitionfinance.data.model.StockData
import com.unimib.ignitionfinance.data.model.user.DailyReturn
import com.unimib.ignitionfinance.data.model.user.Product
import com.unimib.ignitionfinance.data.repository.interfaces.StockRepository
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
            Log.d("BuildDatasetUseCase", "Step 1: Get product list")
            val productsResult = getProductListUseCase.execute(BuildConfig.ALPHAVANTAGE_API_KEY).first()
            val products = productsResult.getOrElse {
                emit(Result.failure(it))
                return@flow
            }
            Log.d("BuildDatasetUseCase", "Products obtained: ${products.map { it.ticker }}")

            Log.d("BuildDatasetUseCase", "Step 2: Fetch historical data")
            val historicalDataResult = fetchHistoricalDataUseCase.execute(apiKey).first()
            val historicalDataList = historicalDataResult.getOrElse {
                emit(Result.failure(it))
                return@flow
            }
            Log.d("BuildDatasetUseCase", "Historical data received: ${historicalDataList.size} items")

            Log.d("BuildDatasetUseCase", "Step 3: Validate dataset")
            val dailyReturns: List<DailyReturn> = when (DatasetValidator.validate(historicalDataList)) {
                is DatasetValidationResult.Success -> {
                    Log.d("BuildDatasetUseCase", "Dataset validation SUCCESS, processing data for products")
                    processData(products, historicalDataList)
                }
                is DatasetValidationResult.Failure -> {
                    Log.d("BuildDatasetUseCase", "Dataset validation FAILURE, using fallback S&P 500 data")
                    val sp500Result = stockRepository.fetchStockData("^GSPC", apiKey).first()
                    val sp500Data = sp500Result.getOrElse { error ->
                        emit(Result.failure(error))
                        return@flow
                    }
                    Log.d("BuildDatasetUseCase", "S&P 500 data received: ${sp500Data.size} entries")

                    // Calcola il capitale totale dai prodotti
                    val totalCapital = calculateTotalCapital(products)
                    Log.d("BuildDatasetUseCase", "Total capital calculated: $totalCapital")

                    val fallbackData = listOf(sp500Data)
                    val fallbackTickers = listOf("^GSPC")
                    val fallbackCapitals = mapOf("^GSPC" to totalCapital)

                    val fallbackDailyReturns = dailyReturnCalculator.calculateDailyReturns(
                        historicalDataList = fallbackData,
                        products = fallbackTickers,
                        productCapitals = fallbackCapitals
                    )
                    Log.d("BuildDatasetUseCase", "Fallback daily returns calculated: ${fallbackDailyReturns.size} items")
                    fallbackDailyReturns
                }
            }

            Log.d("BuildDatasetUseCase", "Step 4: Save dataset, daily returns count: ${dailyReturns.size}")
            val saveResult = saveDatasetUseCase.execute(dailyReturns).first()
            saveResult.getOrElse { error ->
                Log.e("BuildDatasetUseCase", "Error saving dataset: $error")
                emit(Result.failure(error))
                return@flow
            }

            Log.d("BuildDatasetUseCase", "Step 5: Emit final result with ${dailyReturns.size} daily returns")
            emit(Result.success(dailyReturns))

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("BuildDatasetUseCase", "Exception in execute: ${e.message}")
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

    /**
     * Processa i dati storici validi per ciascun prodotto.
     * Estrae le mappe dai Pair e le passa al DailyReturnCalculator.
     */
    private fun processData(
        products: List<Product>,
        historicalData: List<Pair<String, Map<String, StockData>>>
    ): List<DailyReturn> {
        val productTickers = products.map { it.ticker }
        val productCapitals = products.associate { product ->
            product.ticker to try {
                BigDecimal(product.amount)
            } catch (_: NumberFormatException) {
                BigDecimal.ZERO
            }
        }
        Log.d("BuildDatasetUseCase", "Processing data for products: $productTickers")

        val historicalDataMaps: List<Map<String, StockData>> = historicalData.map { it.second }
        val dailyReturns = dailyReturnCalculator.calculateDailyReturns(
            historicalDataList = historicalDataMaps,
            products = productTickers,
            productCapitals = productCapitals
        )
        Log.d("BuildDatasetUseCase", "Daily returns calculated: ${dailyReturns.size}")
        return dailyReturns
    }
}
