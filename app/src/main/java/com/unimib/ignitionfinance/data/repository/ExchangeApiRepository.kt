package com.unimib.ignitionfinance.data.repository

import com.unimib.ignitionfinance.data.remote.exchange_api.ExchangeApiResponseData
import com.unimib.ignitionfinance.data.remote.exchange_api.ExchangeApiService
import com.unimib.ignitionfinance.data.remote.exchange_api.SeriesData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class ExchangeRateRepository(private val apiService: ExchangeApiService) {

    suspend fun fetch(baseCurrency: String): Result<Map<String, List<Double>>> {
        return try {
            // Ottieni le chiavi della serie per USD/EUR e CHF/EUR
            val usdSeriesKey = "D.USD.EUR.SP00.A"
            val chfSeriesKey = "D.CHF.EUR.SP00.A"

            // Esegui entrambe le richieste per le due valute (USD e CHF)
            val usdRatesResult = fetchExchangeRates(usdSeriesKey)
            val chfRatesResult = fetchExchangeRates(chfSeriesKey)

            // Verifica che entrambe le richieste siano andate a buon fine
            if (usdRatesResult.isSuccess && chfRatesResult.isSuccess) {
                // Crea una mappa di tassi di cambio per USD e CHF
                val result = mapOf(
                    "USD" to (usdRatesResult.getOrNull() ?: emptyList()), // Usa emptyList se null
                    "CHF" to (chfRatesResult.getOrNull() ?: emptyList())  // Usa emptyList se null
                )
                // Restituisci il risultato
                Result.success(result)
            } else {
                Result.failure(Exception("Failed to fetch exchange rates"))
            }
        } catch (e: Exception) {
            Result.failure(e) // Gestione delle eccezioni
        }
    }


    // Metodo per recuperare i tassi di cambio per una determinata chiave della serie
    private suspend fun fetchExchangeRates(seriesKey: String): Result<List<Double>> {
        return try {
            val response = apiService.getExchangeRate(seriesKey = seriesKey)

            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                val cleanedData = process(data, seriesKey)
                Result.success(cleanedData)
            } else {
                Result.failure(Exception("Failed to fetch data: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e) // Gestisce eccezioni in caso di errore di rete
        }
    }

    // Metodo per processare i dati e restituirli in un formato più semplice da usare
    private fun process(data: ExchangeApiResponseData, seriesKey: String): List<Double> {
        // Estrai i dati da `dataSets`, che contiene il tasso di cambio
        val dataSet = data.dataSets.firstOrNull() ?: return emptyList()

        // Assumi che il tasso di cambio sia nella chiave specificata (ad esempio "D.USD.EUR.SP00.A")
        val seriesData: SeriesData? = dataSet.series[seriesKey]

        // Controlla se la serie esiste
        if (seriesData == null) {
            throw Exception("No series data found for the given key: $seriesKey")
        }

        // Estrai le osservazioni, che dovrebbero essere mappate per periodo di tempo
        val observations = seriesData.observations
        val values = mutableListOf<Double>()

        // Estrai solo i valori numerici validi dalle osservazioni
        observations.forEach { (_, observationList) ->
            observationList.forEach { value ->
                // Aggiungi solo i valori non nulli
                value?.let {
                    values.add(it)
                }
            }
        }

        // Ritornare la lista dei valori numerici in formato più semplice
        return values
    }
}
