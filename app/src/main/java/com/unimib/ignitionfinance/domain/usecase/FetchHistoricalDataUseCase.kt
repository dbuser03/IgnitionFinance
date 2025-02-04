package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.BuildConfig
import com.unimib.ignitionfinance.data.model.StockData
import com.unimib.ignitionfinance.data.repository.interfaces.StockRepository
import com.unimib.ignitionfinance.domain.usecase.networth.invested.GetProductListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class FetchHistoricalDataUseCase @Inject constructor(
    private val getProductListUseCase: GetProductListUseCase,
    private val stockRepository: StockRepository,
    private val fetchSearchStockDataUseCase: FetchSearchStockDataUseCase
) {
    fun execute(apiKey: String): Flow<Result<List<Pair<String, Map<String, StockData>>>>> = flow {
        try {
            if (apiKey.isEmpty()) {
                throw IllegalArgumentException("API key cannot be empty")
            }

            val productsResult = getProductListUseCase.execute(BuildConfig.ALPHAVANTAGE_API_KEY).first()
            val products = productsResult.getOrNull() ?: run {
                val error = productsResult.exceptionOrNull() ?: Exception("Failed to get products")
                emit(Result.failure(error))
                return@flow
            }

            if (products.isEmpty()) {
                emit(Result.failure(NoSuchElementException("No products found")))
                return@flow
            }

            val historicalDataList = mutableListOf<Pair<String, Map<String, StockData>>>()

            for (product in products) {
                try {
                    val symbol = if (product.symbol.isNotEmpty()) {
                        product.symbol
                    } else {
                        val searchResult = fetchSearchStockDataUseCase.execute(product.ticker, apiKey).first()
                        searchResult.getOrNull()?.symbol
                            ?: throw NoSuchElementException("Symbol not found for product: ${product.ticker}")
                    }

                    val currency = if (product.currency.isNotEmpty()) {
                        product.currency
                    } else {
                        val searchResult = fetchSearchStockDataUseCase.execute(product.ticker, apiKey).first()
                        searchResult.getOrNull()?.currency
                            ?: throw NoSuchElementException("Currency not found for product: ${product.ticker}")
                    }

                    val stockDataResult = stockRepository.fetchStockData(symbol, apiKey).first()
                    stockDataResult.fold(
                        onSuccess = { stockData ->
                            historicalDataList.add(Pair(currency, stockData))
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