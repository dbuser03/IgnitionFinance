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
    // Parametro esterno per la chiave API, per flessibilità e sicurezza
    fun execute(apiKey: String): Flow<Result<List<Map<String, StockData>>>> = flow {
        try {
            // Recupera prima la lista dei prodotti
            val productsResult = getProductListUseCase.execute().first()

            // Gestisci il risultato della lista prodotti
            val products = productsResult.getOrElse {
                // In caso di errore, emetti un errore
                emit(Result.failure(it))
                return@flow
            }

            // Mappa per contenere gli storici dei prodotti
            val historicalDataList = mutableListOf<Map<String, StockData>>()

            // Recupera lo storico per ogni prodotto
            products.forEach { product ->
                // Recupera lo storico per il singolo simbolo
                val stockDataResult = stockRepository.fetchStockData(product.symbol, apiKey).first()

                stockDataResult.onSuccess { stockData ->
                    historicalDataList.add(stockData)
                }.onFailure {
                    // Gestisci eventuali errori per un singolo prodotto
                    // Puoi scegliere di lanciare un'eccezione o continuare
                    emit(Result.failure(it))
                }
            }

            // Emetti il risultato finale
            emit(Result.success(historicalDataList))

        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}