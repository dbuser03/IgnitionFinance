package com.unimib.ignitionfinance.data.repository

import com.unimib.ignitionfinance.data.remote.exchange_api.ExchangeApiResponseData
import com.unimib.ignitionfinance.data.remote.exchange_api.ExchangeApiService
import com.unimib.ignitionfinance.data.remote.exchange_api.SeriesData
import retrofit2.Response

// Repository per recuperare i tassi di cambio
class ExchangeRateRepository(private val apiService: ExchangeApiService) {

    // Metodo che recupera i tassi di cambio e li converte in un formato più semplice
    suspend fun fetchExchangeRateData(): Result<List<ExchangeRate>> {
        // Ottieni i tassi di cambio per USD/EUR e CHF/EUR
        val usdRatesResult = fetchExchangeRates("D.USD.EUR.SP00.A")
        val chfRatesResult = fetchExchangeRates("D.CHF.EUR.SP00.A")

        // Verifica se entrambe le richieste sono andate a buon fine
        return if (usdRatesResult.isSuccess && chfRatesResult.isSuccess) {
            val result = usdRatesResult.getOrNull()?.plus(chfRatesResult.getOrNull().orEmpty())
            Result.success(result ?: emptyList())
        } else {
            // Se uno dei due risultati non è riuscito, restituisci un errore
            Result.failure(Exception("Failed to fetch exchange rates"))
        }
    }

    // Metodo che recupera i tassi di cambio per una determinata chiave della serie
    private suspend fun fetchExchangeRates(seriesKey: String): Result<List<ExchangeRate>> {
        // Effettua la richiesta all'API per i tassi di cambio
        val response = apiService.getExchangeRate(seriesKey = seriesKey)

        // Controlla se la risposta è valida
        return if (response.isSuccessful && response.body() != null) {
            val data = response.body()!!
            val cleanedData = processExchangeRateData(data, seriesKey)
            Result.success(cleanedData)
        } else {
            // Se la risposta non è valida, restituisci un errore
            Result.failure(Exception("Failed to fetch data: ${response.code()}"))
        }
    }

    private fun processExchangeRateData(data: ExchangeApiResponseData, seriesKey: String): List<ExchangeRate> {
        val processedData = mutableListOf<ExchangeRate>()

        // Itera sui dataset (come nella struttura dell'inflazione)
        data.dataSets.forEach { dataSet ->
            dataSet.series.forEach { (key, seriesData) ->
                // Assicurati che la chiave della serie corrisponda a quella desiderata
                if (key == seriesKey) {
                    // Itera sulle osservazioni della serie (date, valori)
                    seriesData.observations.forEach { (date, values) ->
                        // Aggiungi solo il primo valore trovato per ogni data
                        values.firstOrNull()?.let { rate ->
                            // Aggiungi il tasso di cambio come un nuovo ExchangeRate
                            processedData.add(ExchangeRate(date, rate))
                        }
                    }
                }
            }
        }

        return processedData
    }

}

// Data class che rappresenta un singolo tasso di cambio
data class ExchangeRate(
    val date: String,  // Data dell'osservazione
    val rate: Double   // Tasso di cambio
)
