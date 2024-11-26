package com.unimib.ignitionfinance.data.repository

import com.unimib.ignitionfinance.data.remote.inflation_api.InflationApiResponseData
import com.unimib.ignitionfinance.data.remote.inflation_api.InflationApiService
import retrofit2.Response

// Definiamo il repository per recuperare i dati dell'inflazione
class InflationRepository(private val inflationApiService: InflationApiService) {

    // Metodo che recupera i dati dell'inflazione e li converte in un formato più semplice
    suspend fun fetchInflationData(): Result<List<InflationData>> {
        // Effettua la chiamata all'API
        val response = inflationApiService.getInflationData()

        // Verifica se la risposta è valida e contiene i dati
        if (response.isSuccessful) {
            val inflationData = response.body()

            // Se la risposta è valida, processa i dati
            if (inflationData != null) {
                return Result.success(processInflationData(inflationData))
            }
        }

        // Se la risposta è fallita, restituisci un errore
        return Result.failure(Throwable("Failed to fetch inflation data"))
    }

    // Metodo che processa i dati dell'inflazione
    private fun processInflationData(inflationData: InflationApiResponseData): List<InflationData> {
        val processedData = mutableListOf<InflationData>()

        // Itera sulle serie e processa i valori di inflazione
        inflationData.dataSets.forEach { dataSet ->
            dataSet.series.forEach { (seriesKey, seriesData) ->
                seriesData.observations.forEach { (date, values) ->
                    // Aggiunge un'osservazione dei dati di inflazione
                    values.firstOrNull()?.let { inflationRate ->
                        processedData.add(InflationData(date, inflationRate))
                    }
                }
            }
        }

        return processedData
    }
}

data class InflationData(
    val date: String, // La data della rilevazione
    val inflationRate: Double // Il tasso di inflazione
)
