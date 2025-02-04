package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.model.StockData
import com.unimib.ignitionfinance.data.repository.interfaces.SearchStockRepository
import com.unimib.ignitionfinance.data.repository.interfaces.StockRepository
import com.unimib.ignitionfinance.domain.usecase.networth.GetProductListUseCase
import com.unimib.ignitionfinance.domain.usecase.networth.UpdateProductUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class FetchHistoricalDataUseCase @Inject constructor(
    private val getProductListUseCase: GetProductListUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
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
                    var symbol = product.symbol
                    var currency = product.currency

                    if (symbol.isEmpty() || currency.isEmpty()) {
                        val searchResult = searchStockRepository.fetchSearchStockData(product.ticker, apiKey).first()
                        val searchData = searchResult.getOrNull()?.firstOrNull() ?: run {
                            emit(Result.failure(NoSuchElementException("Symbol not found for product: ${product.ticker}")))
                            return@flow
                        }

                        symbol = searchData.symbol
                        currency = searchData.currency

                        val updatedProduct = product.copy(
                            symbol = symbol,
                            currency = currency
                        )
                        val updateResult = updateProductUseCase.execute(updatedProduct).first()
                        updateResult.getOrElse { throw it }
                    }

                    var historicalData = product.historicalData

                    if (historicalData.isEmpty()) {
                        val stockDataResult = stockRepository.fetchStockData(symbol, apiKey).first()
                        stockDataResult.fold(
                            onSuccess = { stockData ->
                                historicalData = stockData
                            },
                            onFailure = { exception ->
                                emit(Result.failure(exception))
                                return@flow
                            }
                        )
                        val updatedProductWithHist = product.copy(
                            symbol = symbol,
                            currency = currency,
                            historicalData = historicalData
                        )
                        val updateResult2 = updateProductUseCase.execute(updatedProductWithHist).first()
                        updateResult2.getOrElse { throw it }
                    }

                    historicalDataList.add(historicalData)
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