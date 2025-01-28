package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.model.StockData
import com.unimib.ignitionfinance.data.model.user.Product
import com.unimib.ignitionfinance.data.repository.interfaces.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchHistoricalDataUseCase @Inject constructor(
    private val getProductListUseCase: GetProductListUseCase,
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

            products.forEach { product ->
                val stockDataResult = stockRepository.fetchStockData(product.symbol, apiKey).first()

                stockDataResult.onSuccess { stockData ->
                    historicalDataList.add(stockData)
                }.onFailure {
                    emit(Result.failure(it))
                }
            }

            emit(Result.success(historicalDataList))

        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}