package com.unimib.ignitionfinance.data.remote.json_restructuring

import android.os.Build
import androidx.annotation.RequiresApi
import org.json.JSONObject
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

// Data class per rappresentare il tasso di cambio giornaliero
data class DailyExchangeRate(
    val date: OffsetDateTime,
    val exchangeRate: Double
)

// Classe di utilità per gestire i tassi di cambio giornalieri
class ExchangeRateUtil {

    // Mappa dinamica per memorizzare i tassi di cambio con la data come chiave
    private val exchangeRates: HashMap<OffsetDateTime, DailyExchangeRate> = hashMapOf()

    // Funzione per aggiornare o aggiungere un nuovo tasso di cambio
    @RequiresApi(Build.VERSION_CODES.O)
    fun addExchangeRate(date: String, exchangeRate: Double) {
        try {
            // Converte la data in OffsetDateTime per gestire il fuso orario
            val formattedDate = OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)

            // Aggiunge o aggiorna il tasso di cambio per quella data
            exchangeRates[formattedDate] = DailyExchangeRate(formattedDate, exchangeRate)
        } catch (e: Exception) {
            // Gestione dell'errore in caso di formati data non validi
            e.printStackTrace()
        }
    }

    // Funzione per ottenere il tasso di cambio per una data specifica
    @RequiresApi(Build.VERSION_CODES.O)
    fun getExchangeRate(date: String): Double? {
        try {
            // Converte la data in OffsetDateTime
            val formattedDate = OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)

            // Restituisce il tasso di cambio, se presente
            return exchangeRates[formattedDate]?.exchangeRate
        } catch (e: Exception) {
            // Gestione dell'errore in caso di formati data non validi
            e.printStackTrace()
            return null
        }
    }

    // Funzione per ottenere tutti i tassi di cambio
    fun getAllExchangeRates(): List<DailyExchangeRate> {
        return exchangeRates.values.toList()
    }
}

// Funzione per estrarre e aggiungere il tasso di cambio dai dati JSON
@RequiresApi(Build.VERSION_CODES.O)
fun extractAndSaveExchangeRate(json: String, exchangeRateUtil: ExchangeRateUtil) {
    try {
        // Parsing del JSON
        val jsonObject = JSONObject(json)
        val dataSets = jsonObject.getJSONArray("dataSets")

        // Estrarre la data e il tasso di cambio
        for (i in 0 until dataSets.length()) {
            val dataSet = dataSets.getJSONObject(i)
            val series = dataSet.getJSONObject("series")
            val seriesKey = "EXR.D.CHF.EUR.SP00.A"

            // Verifica che la serie esista nel JSON
            if (series.has(seriesKey)) {
                val observations = series.getJSONObject(seriesKey).getJSONObject("observations")
                val observation = observations.getJSONArray("0") // Prendiamo il primo valore (giornaliero)

                // Verifica se il tasso di cambio è valido
                if (observation.length() > 1 && observation.get(1) != JSONObject.NULL) {
                    val exchangeRate = observation.getDouble(1) // Supponiamo che il tasso di cambio sia nel secondo indice
                    val validFrom = dataSet.getString("validFrom")

                    // Aggiungi il tasso di cambio alla struttura dati
                    exchangeRateUtil.addExchangeRate(validFrom, exchangeRate)
                }
            }
        }
    } catch (e: Exception) {
        // Gestione degli errori nel parsing
        e.printStackTrace()
    }
}
