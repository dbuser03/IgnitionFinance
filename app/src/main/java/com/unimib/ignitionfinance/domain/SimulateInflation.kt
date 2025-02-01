package com.unimib.ignitionfinance.domain

import com.unimib.ignitionfinance.data.model.InflationData
import com.unimib.ignitionfinance.data.repository.interfaces.InflationRepository
import com.unimib.ignitionfinance.domain.usecase.inflation.FetchInflationUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import javax.inject.Inject

fun nextGaussian(): Double {
    val u1 = Random.nextDouble()
    val u2 = Random.nextDouble()
    return sqrt(-2.0 * ln(u1)) * cos(2.0 * PI * u2)
}

class InflationCalculator @Inject constructor(
    private val fetchInflationUseCase: FetchInflationUseCase,
    private val numSimulazioni: Int,
    private val inflazioneMedia: Double
) {
    private suspend fun getInflazioneReale(): DoubleArray {
        return try {
            val inflationData = fetchInflationUseCase.execute().first()
            inflationData.fold(
                onSuccess = { data ->
                    data.values.map { it / 100.0 }.toDoubleArray()
                },
                onFailure = { exception ->
                    println("Errore nel recupero dei dati dell'inflazione: ${exception.message}")
                    doubleArrayOf() // Return empty array in case of error
                }
            )
        } catch (e: Exception) {
            println("Errore nel recupero dei dati dell'inflazione: ${e.message}")
            doubleArrayOf() // Return empty array in case of error
        }
    }

    private suspend fun setInflazione(scenarioInflazione: String): Array<DoubleArray> {
        val inflazione = Array(100) { DoubleArray(numSimulazioni) }
        val inflazioneReale = getInflazioneReale()

        if (inflazioneReale.isEmpty()) {
            println("Nessun dato di inflazione disponibile. Uso l'inflazione media.")
            return Array(100) { DoubleArray(numSimulazioni) { inflazioneMedia } }
        }

        when (scenarioInflazione.lowercase()) {
            "reale" -> {
                for (i in 0 until 100) {
                    for (j in 0 until numSimulazioni) {
                        inflazione[i][j] = inflazioneReale[Random.nextInt(inflazioneReale.size)]
                    }
                }
            }

            "reale riscalata" -> {
                val meanReale = inflazioneReale.average()
                val scaleFactor = inflazioneMedia / meanReale
                val inflazioneRiscalata = inflazioneReale.map { it * scaleFactor }.toDoubleArray()

                for (i in 0 until 100) {
                    for (j in 0 until numSimulazioni) {
                        inflazione[i][j] = inflazioneRiscalata[Random.nextInt(inflazioneRiscalata.size)]
                    }
                }
            }

            "lognormale" -> {
                val variance = inflazioneReale.map { it * it }.average() - inflazioneReale.average().pow(2)
                var mu = ln(inflazioneMedia)
                var sigma = ln((1 + sqrt(1 + 4 * variance / exp(2 * mu))) / 2)
                mu = ln(inflazioneMedia) - sigma.pow(2) / 2
                sigma = ln((1 + sqrt(1 + 4 * variance / exp(2 * mu))) / 2)
                mu = ln(inflazioneMedia) - sigma.pow(2) / 2

                for (i in 0 until 100) {
                    for (j in 0 until numSimulazioni) {
                        val z = nextGaussian()
                        inflazione[i][j] = exp(mu + sigma * z)
                    }
                }
            }

            else -> {
                println("Scenario di inflazione non riconosciuto. Uso l'inflazione media.")
                for (i in 0 until 100) {
                    for (j in 0 until numSimulazioni) {
                        inflazione[i][j] = inflazioneMedia
                    }
                }
            }
        }

        val flatInflazione = mutableListOf<Double>()
        for (i in 0 until 100) {
            for (j in 0 until numSimulazioni) {
                flatInflazione.add(inflazione[i][j])
            }
        }
        val media = flatInflazione.average()
        val devSt = sqrt(flatInflazione.map { (it - media).pow(2) }.average())
        println("Media: $media Dev st: $devSt")

        return inflazione
    }

    suspend fun calcolaErosioneLiquidita(liquiditaIniziale: Double, scenarioInflazione: String): Array<DoubleArray> {
        val inflazione = setInflazione(scenarioInflazione)
        val liquiditaErosa = Array(100) { DoubleArray(numSimulazioni) }

        for (j in 0 until numSimulazioni) {
            var liquiditaCorrente = liquiditaIniziale
            for (i in 0 until 100) {
                liquiditaCorrente /= (1 + inflazione[i][j])
                liquiditaErosa[i][j] = liquiditaCorrente
            }
        }

        val liquiditaFinale = mutableListOf<Double>()
        for (j in 0 until numSimulazioni) {
            liquiditaFinale.add(liquiditaErosa[99][j])
        }

        val mediaFinale = liquiditaFinale.average()
        val devStFinale = sqrt(liquiditaFinale.map { (it - mediaFinale).pow(2) }.average())

        println("Statistiche finale dopo 100 anni:")
        println("Liquidita' iniziale: $liquiditaIniziale")
        println("Media liquidita' finale: $mediaFinale")
        println("Deviazione standard finale: $devStFinale")
        println("Perdita media: ${(1 - mediaFinale/liquiditaIniziale) * 100}%")

        return liquiditaErosa
    }
}

suspend fun main() {
    // Crea un'implementazione del repository
    val inflationRepository = object : InflationRepository {
        override suspend fun fetchInflationData(): Flow<Result<List<InflationData>>> = flow {
            // Dati storici dal 1955 al 1987
            val historicalData = mapOf(
                1955 to 2.3, 1956 to 3.4, 1957 to 1.3, 1958 to 2.8, 1959 to -0.4,
                1960 to 2.3, 1961 to 2.1, 1962 to 4.7, 1963 to 7.5, 1964 to 5.9,
                1965 to 4.6, 1966 to 2.3, 1967 to 3.7, 1968 to 1.4, 1969 to 2.6,
                1970 to 5.0, 1971 to 4.8, 1972 to 5.7, 1973 to 10.8, 1974 to 19.1,
                1975 to 17.0, 1976 to 16.8, 1977 to 17.0, 1978 to 12.1, 1979 to 14.8,
                1980 to 21.2, 1981 to 17.8, 1982 to 16.5, 1983 to 14.7, 1984 to 10.8,
                1985 to 9.2, 1986 to 5.8, 1987 to 4.8
            )

            // Dati dal 1988 in poi
            val recentRates = arrayOf(
                5.0, 6.3, 6.5, 6.2, 5.3, 4.7, 4.1, 5.3, 4.0, 2.0,
                2.0, 1.7, 2.5, 2.7, 2.5, 2.7, 2.2, 1.9, 2.1, 1.8,
                3.3, 0.8, 1.5, 2.7, 3.0, 1.2, 0.2, 0.1, -0.1, 1.2,
                1.2, 0.6, -0.2, 1.9, 8.1, 8.7, 3.0
            )

            val historicalInflationData = historicalData.map { (year, rate) ->
                InflationData(year.toString(), rate)
            }

            val recentInflationData = recentRates.mapIndexed { index, rate ->
                InflationData((1988 + index).toString(), rate)
            }

            emit(Result.success(historicalInflationData + recentInflationData))
        }
    }

    // Crea il use case
    val fetchInflationUseCase = FetchInflationUseCase(inflationRepository)

    // Crea il calculator con il use case
    val calculator = InflationCalculator(
        fetchInflationUseCase = fetchInflationUseCase,
        numSimulazioni = 1000,
        inflazioneMedia = 0.03  // 3%
    )

    val liquiditaIniziale = 100000.0 // 100,000 €

    println("\nScenario: Inflazione Reale")
    calculator.calcolaErosioneLiquidita(liquiditaIniziale, "reale")

    println("\nScenario: Inflazione Reale Riscalata")
    calculator.calcolaErosioneLiquidita(liquiditaIniziale, "reale riscalata")

    println("\nScenario: Inflazione Lognormale")
    calculator.calcolaErosioneLiquidita(liquiditaIniziale, "lognormale")
}