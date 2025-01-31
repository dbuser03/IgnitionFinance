package com.unimib.ignitionfinance.domain

import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow

fun nextGaussian(): Double {
    val u1 = Random.nextDouble()
    val u2 = Random.nextDouble()
    return sqrt(-2.0 * ln(u1)) * cos(2.0 * PI * u2)
}

class InflationCalculator(
    private val numSimulazioni: Int,
    private val inflazioneMedia: Double
) {
    private val inflazioneReale = arrayOf(
        2.3, 3.4, 1.3, 2.8, -0.4, 2.3, 2.1, 4.7, 7.5, 5.9, 4.6, 2.3, 3.7, 1.4, 2.6, 5.0, 4.8, 5.7, 10.8, 19.1, 17.0,
        16.8, 17.0, 12.1, 14.8, 21.2, 17.8, 16.5, 14.7, 10.8, 9.2, 5.8, 4.8, 5.0, 6.3, 6.5, 6.2, 5.3, 4.7, 4.1, 5.3,
        4.0, 2.0, 2.0, 1.7, 2.5, 2.7, 2.5, 2.7, 2.2, 1.9, 2.1, 1.8, 3.3, 0.8, 1.5, 2.7, 3.0, 1.2, 0.2, 0.1, -0.1,
        1.2, 1.2, 0.6, -0.2, 1.9, 8.1, 8.7
    ).map { it / 100.0 }.toDoubleArray()

    private fun setInflazione(scenarioInflazione: String): Array<DoubleArray> {
        val inflazione = Array(100) { DoubleArray(numSimulazioni) }

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
                // Calculate variance of real inflation
                val variance = inflazioneReale.map { it * it }.average() - inflazioneReale.average().pow(2)

                // Calculate mu and sigma for lognormal distribution
                var mu = ln(inflazioneMedia)
                var sigma = ln((1 + sqrt(1 + 4 * variance / exp(2 * mu))) / 2)
                mu = ln(inflazioneMedia) - sigma.pow(2) / 2
                sigma = ln((1 + sqrt(1 + 4 * variance / exp(2 * mu))) / 2)
                mu = ln(inflazioneMedia) - sigma.pow(2) / 2

                // Generate lognormal random values
                for (i in 0 until 100) {
                    for (j in 0 until numSimulazioni) {
                        val z = nextGaussian()
                        inflazione[i][j] = exp(mu + sigma * z)
                    }
                }
            }

            else -> {
                println("Ciccio, guarda che non so come gestire l'inflazione!")
                for (i in 0 until 100) {
                    for (j in 0 until numSimulazioni) {
                        inflazione[i][j] = inflazioneMedia
                    }
                }
            }
        }

        // Calculate and print statistics
        //val flatInflazione = inflazione.flatten()
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

    fun calcolaErosioneLiquidita(liquiditaIniziale: Double, scenarioInflazione: String): Array<DoubleArray> {
        // Ottiene la matrice di inflazione dal modello scelto
        val inflazione = setInflazione(scenarioInflazione)

        // Crea una matrice per memorizzare i valori della liquidità erosa
        val liquiditaErosa = Array(100) { DoubleArray(numSimulazioni) }

        // Per ogni simulazione e anno, calcola l'erosione della liquidità
        for (j in 0 until numSimulazioni) {
            var liquiditaCorrente = liquiditaIniziale
            for (i in 0 until 100) {
                // La liquidità viene erosa dall'inflazione
                liquiditaCorrente /= (1 + inflazione[i][j])
                liquiditaErosa[i][j] = liquiditaCorrente
            }
        }

        // Calcola e stampa alcune statistiche
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

fun main() {
    val calculator = InflationCalculator(
        numSimulazioni = 1000,
        inflazioneMedia = 0.03  // 3%
    )

    val liquiditaIniziale = 100000.0 // 100,000 €

    // Testa tutti gli scenari
    println("\nScenario: Inflazione Reale")
    calculator.calcolaErosioneLiquidita(liquiditaIniziale, "reale")

    println("\nScenario: Inflazione Reale Riscalata")
    calculator.calcolaErosioneLiquidita(liquiditaIniziale, "reale riscalata")

    println("\nScenario: Inflazione Lognormale")
    calculator.calcolaErosioneLiquidita(liquiditaIniziale, "lognormale")
}

//prende liquidità, per testare se funzionano i modelli --> restituisce un array con i valori della liquidità eroso
//dal % di inflazione