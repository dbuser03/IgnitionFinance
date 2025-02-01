package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.model.StockData
import com.unimib.ignitionfinance.data.repository.interfaces.SearchStockRepository
import com.unimib.ignitionfinance.data.repository.interfaces.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchHistoricalDataUseCase @Inject constructor(
    private val getProductListUseCase: GetProductListUseCase,
    private val searchStockRepository: SearchStockRepository,
    private val stockRepository: StockRepository
) {
    fun execute(apiKey: String): Flow<Result<List<Map<String, StockData>>>> = flow {
        try {
            val productsResult = getProductListUseCase.execute().first()

            val products = productsResult.getOrElse {
                emit(Result.failure(it))
                return@flow
            }

            val historicalDataList = mutableListOf<Map<String, StockData>>()

            for (product in products) {
                // Step 1: Search for the symbol of the product
                val searchResult = searchStockRepository.fetchSearchStockData(product.ticker, apiKey).first()
                val symbol = searchResult.getOrElse {
                    emit(Result.failure(it))
                    return@flow
                }.firstOrNull()?.symbol

                if (symbol == null) {
                    emit(Result.failure(Throwable("Symbol not found for product: ${product.ticker}")))
                    return@flow
                }

                // Step 2: Fetch historical data using the found symbol
                val stockDataResult = stockRepository.fetchStockData(symbol, apiKey).first()

                stockDataResult.onSuccess { stockData ->
                    historicalDataList.add(stockData)
                }.onFailure {
                    emit(Result.failure(it))
                    return@flow
                }
            }

            emit(Result.success(historicalDataList))

        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
