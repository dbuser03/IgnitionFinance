package com.unimib.ignitionfinance.domain

import com.unimib.ignitionfinance.domain.usecase.inflation.FetchInflationUseCase
import kotlinx.coroutines.flow.first
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

/*suspend fun main() {
    val calculator = InflationCalculator(
        fetchInflationUseCase = // inject your FetchInflationUseCase here
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
}*/