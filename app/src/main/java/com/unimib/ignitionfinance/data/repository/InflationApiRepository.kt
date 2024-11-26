package com.unimib.ignitionfinance.data.repository

import com.unimib.ignitionfinance.data.remote.inflation_api.InflationApiResponseData
import com.unimib.ignitionfinance.data.remote.inflation_api.InflationApiService
import retrofit2.Response

class InflationRepository(private val inflationApiService: InflationApiService) {

    // Metodo che recupera i dati dell'inflazione e li converte in un formato più semplice
    suspend fun fetchInflationData(): Result<Map<String, List<Double>>> {
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
    private fun processInflationData(inflationData: InflationApiResponseData): Map<String, List<Double>> {
        val processedData = mutableMapOf<String, List<Double>>()

        // Itera sulle serie e processa i valori di inflazione
        inflationData.dataSets.forEach { dataSet ->
            dataSet.series.forEach { (seriesKey, seriesData) ->
                val inflationValues = seriesData.observations.mapNotNull { it.value.firstOrNull() }
                processedData[seriesKey] = inflationValues
            }
        }

        return processedData
    }
}
