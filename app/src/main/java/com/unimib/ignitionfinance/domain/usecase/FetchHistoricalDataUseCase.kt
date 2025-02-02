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
            if (apiKey.isEmpty()) {
                throw IllegalArgumentException("API key cannot be empty")
            }

            val productsResult = getProductListUseCase.execute().first()
            val products = productsResult.getOrNull() ?: run {
                val error = productsResult.exceptionOrNull() ?: Exception("Failed to get products")
                emit(Result.failure(error))
                return@flow
            }

            if (products.isEmpty()) {
                emit(Result.failure(NoSuchElementException("No products found")))
                return@flow
            }

            val historicalDataList = mutableListOf<Map<String, StockData>>()

            for (product in products) {
                try {
                    // Step 1: Search for the symbol of the product
                    val searchResult = searchStockRepository.fetchSearchStockData(product.ticker, apiKey).first()
                    val symbol = searchResult.getOrNull()?.firstOrNull()?.symbol ?: run {
                        emit(Result.failure(NoSuchElementException("Symbol not found for product: ${product.ticker}")))
                        return@flow
                    }

                    // Step 2: Fetch historical data using the found symbol
                    val stockDataResult = stockRepository.fetchStockData(symbol, apiKey).first()
                    stockDataResult.fold(
                        onSuccess = { stockData ->
                            historicalDataList.add(stockData)
                        },
                        onFailure = { exception ->
                            emit(Result.failure(exception))
                            return@flow
                        }
                    )
                } catch (e: Exception) {
                    emit(Result.failure(Exception("Error processing product ${product.ticker}: ${e.message}")))
                    return@flow
                }
            }

            if (historicalDataList.isEmpty()) {
                emit(Result.failure(NoSuchElementException("No historical data found for any product")))
            } else {
                emit(Result.success(historicalDataList))
            }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}