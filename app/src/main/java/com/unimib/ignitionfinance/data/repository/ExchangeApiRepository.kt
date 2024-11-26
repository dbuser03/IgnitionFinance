package com.unimib.ignitionfinance.data.repository

import com.unimib.ignitionfinance.data.remote.exchange_api.ExchangeApiResponseData
import com.unimib.ignitionfinance.data.remote.exchange_api.ExchangeApiService
import com.unimib.ignitionfinance.data.remote.exchange_api.SeriesData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class ExchangeRateRepository(private val apiService: ExchangeApiService) {

    // Metodo per recuperare i tassi di cambio e convertirli in un formato più semplice
    suspend fun fetch(baseCurrency: String): Result<Map<String, List<ExchangeRate>>> {
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
    private suspend fun fetchExchangeRates(seriesKey: String): Result<List<ExchangeRate>> {
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
    private fun process(data: ExchangeApiResponseData, seriesKey: String): List<ExchangeRate> {
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
        val values = mutableListOf<ExchangeRate>()

        // Estrai solo i valori numerici validi dalle osservazioni
        observations.forEach { (date, observationList) ->
            observationList.forEach { value ->
                // Aggiungi solo i valori non nulli
                value?.let {
                    // Aggiungi la data e il tasso di cambio come ExchangeRate
                    values.add(ExchangeRate(date, it))
                }
            }
        }

        // Ritornare la lista dei valori numerici in formato più semplice
        return values
    }
}

// Data class che rappresenta un singolo tasso di cambio
data class ExchangeRate(
    val date: String,  // Data dell'osservazione
    val rate: Double   // Tasso di cambio
)
