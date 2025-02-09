package com.unimib.ignitionfinance.data.repository.implementation

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unimib.ignitionfinance.data.remote.mapper.StockMapper
import com.unimib.ignitionfinance.data.remote.model.StockData
import com.unimib.ignitionfinance.data.remote.response.TimeSeriesData
import com.unimib.ignitionfinance.data.remote.service.StockService
import com.unimib.ignitionfinance.data.repository.interfaces.StockRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class StockRepositoryImpl @Inject constructor(
    private val stockService: StockService,
    private val stockApiMapper: StockMapper,
    @ApplicationContext private val context: Context
) : StockRepository {

    override suspend fun fetchStockData(symbol: String, apiKey: String): Flow<Result<Map<String, StockData>>> = flow {
        try {
            val response = stockService.getStockData(symbol = symbol, apiKey = apiKey)
            if (response.isSuccessful) {
                val stockData = response.body()
                Log.d("StockRepository", "Response body: $stockData")
                if (stockData != null) {
                    // Legge il file JSON dagli assets
                    val extraJson = readAssetFile("extra_data.json")

                    // Parsing del JSON in una Map<String, TimeSeriesData>
                    val type = object : TypeToken<Map<String, TimeSeriesData>>() {}.type
                    val extraData: Map<String, TimeSeriesData> = Gson().fromJson(extraJson, type)

                    // Filtra i dati con data precedente al 1999-11-01
                    val filteredExtraData = extraData.filterKeys { date ->
                        date < "1999-11-01"
                    }

                    // Unisci i dati extra con quelli esistenti
                    val mergedTimeSeries = stockData.timeSeries.toMutableMap().apply {
                        putAll(filteredExtraData)
                    }

                    // Se il modello è immutabile, crea una nuova istanza
                    val updatedStockData = stockData.copy(timeSeries = mergedTimeSeries)

                    // Mappa i dati aggiornati al dominio
                    val mappedData = stockApiMapper.mapToDomain(updatedStockData)
                    emit(Result.success(mappedData))
                } else {
                    Log.e("StockRepository", "Error: Empty response body")
                    emit(Result.failure(Throwable("Error: Empty response body")))
                }
            } else {
                Log.e("StockRepository", "Failed to fetch stock data - HTTP ${response.code()}")
                emit(Result.failure(Throwable("Failed to fetch stock data")))
            }
        } catch (e: Exception) {
            Log.e("StockRepository", "Exception: ${e.message}", e)
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    // Funzione di utilità per leggere un file dagli assets
    private fun readAssetFile(fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }
}
