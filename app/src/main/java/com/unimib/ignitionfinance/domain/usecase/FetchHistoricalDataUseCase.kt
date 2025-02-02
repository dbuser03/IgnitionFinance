package com.unimib.ignitionfinance.domain.usecase

import android.util.Log
import com.unimib.ignitionfinance.data.model.StockData
import com.unimib.ignitionfinance.data.repository.interfaces.SearchStockRepository
import com.unimib.ignitionfinance.data.repository.interfaces.StockRepository
import com.unimib.ignitionfinance.domain.usecase.networth.GetProductListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class FetchHistoricalDataUseCase @Inject constructor(
    private val getProductListUseCase: GetProductListUseCase,
    private val searchStockRepository: SearchStockRepository,
    private val stockRepository: StockRepository
) {
    fun execute(apiKey: String): Flow<Result<List<Map<String, StockData>>>> = flow {
        try {
            Log.d("FetchHistoricalDataUseCase", "Starting execution with API Key: $apiKey")

            if (apiKey.isEmpty()) {
                Log.e("FetchHistoricalDataUseCase", "API key is empty")
                throw IllegalArgumentException("API key cannot be empty")
            }

            val productsResult = getProductListUseCase.execute().first()
            Log.d("FetchHistoricalDataUseCase", "Fetched products result: $productsResult")

            val products = productsResult.getOrNull() ?: run {
                val error = productsResult.exceptionOrNull() ?: Exception("Failed to get products")
                Log.e("FetchHistoricalDataUseCase", "Error getting products: ${error.message}")
                emit(Result.failure(error))
                return@flow
            }

            Log.d("FetchHistoricalDataUseCase", "Found ${products.size} products")

            if (products.isEmpty()) {
                Log.e("FetchHistoricalDataUseCase", "No products found")
                emit(Result.failure(NoSuchElementException("No products found")))
                return@flow
            }

            val historicalDataList = mutableListOf<Map<String, StockData>>()

            for (product in products) {
                try {
                    Log.d("FetchHistoricalDataUseCase", "Processing product: ${product.ticker}")

                    // Step 1: Search for the symbol of the product
                    val searchResult = searchStockRepository.fetchSearchStockData(product.ticker, apiKey).first()
                    Log.d("FetchHistoricalDataUseCase", "Search result for ${product.ticker}: $searchResult")

                    val symbol = searchResult.getOrNull()?.firstOrNull()?.symbol ?: run {
                        Log.e("FetchHistoricalDataUseCase", "Symbol not found for ${product.ticker}")
                        emit(Result.failure(NoSuchElementException("Symbol not found for product: ${product.ticker}")))
                        return@flow
                    }

                    Log.d("FetchHistoricalDataUseCase", "Found symbol for ${product.ticker}: $symbol")

                    // Step 2: Fetch historical data using the found symbol
                    val stockDataResult = stockRepository.fetchStockData(symbol, apiKey).first()
                    Log.d("FetchHistoricalDataUseCase", "Stock data result for $symbol: $stockDataResult")

                    stockDataResult.fold(
                        onSuccess = { stockData ->
                            Log.d("FetchHistoricalDataUseCase", "Successfully fetched stock data for $symbol")
                            historicalDataList.add(stockData)
                        },
                        onFailure = { exception ->
                            Log.e("FetchHistoricalDataUseCase", "Error fetching stock data for $symbol: ${exception.message}")
                            emit(Result.failure(exception))
                            return@flow
                        }
                    )
                } catch (e: Exception) {
                    Log.e("FetchHistoricalDataUseCase", "Error processing product ${product.ticker}: ${e.message}")
                    emit(Result.failure(Exception("Error processing product ${product.ticker}: ${e.message}")))
                    return@flow
                }
            }

            if (historicalDataList.isEmpty()) {
                Log.e("FetchHistoricalDataUseCase", "No historical data found for any product")
                emit(Result.failure(NoSuchElementException("No historical data found for any product")))
            } else {
                Log.d("FetchHistoricalDataUseCase", "Successfully fetched historical data for ${historicalDataList.size} products")
                emit(Result.success(historicalDataList))
            }

        } catch (e: CancellationException) {
            Log.w("FetchHistoricalDataUseCase", "Execution cancelled")
            throw e
        } catch (e: Exception) {
            Log.e("FetchHistoricalDataUseCase", "Unexpected error: ${e.message}")
            emit(Result.failure(e))
        }
    }
}