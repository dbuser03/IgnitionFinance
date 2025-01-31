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
    private val num_simulazioni: Int,
    private val inflazione_media: Double
) {
    private val inflazione_reale = arrayOf(
        2.3, 3.4, 1.3, 2.8, -0.4, 2.3, 2.1, 4.7, 7.5, 5.9, 4.6, 2.3, 3.7, 1.4, 2.6, 5.0, 4.8, 5.7, 10.8, 19.1, 17.0,
        16.8, 17.0, 12.1, 14.8, 21.2, 17.8, 16.5, 14.7, 10.8, 9.2, 5.8, 4.8, 5.0, 6.3, 6.5, 6.2, 5.3, 4.7, 4.1, 5.3,
        4.0, 2.0, 2.0, 1.7, 2.5, 2.7, 2.5, 2.7, 2.2, 1.9, 2.1, 1.8, 3.3, 0.8, 1.5, 2.7, 3.0, 1.2, 0.2, 0.1, -0.1,
        1.2, 1.2, 0.6, -0.2, 1.9, 8.1, 8.7
    ).map { it / 100.0 }.toDoubleArray()

    fun setInflazione(scenario_inflazione: String): Array<DoubleArray> {
        val inflazione = Array(100) { DoubleArray(num_simulazioni) }

        when (scenario_inflazione.lowercase()) {
            "fissa" -> {
                for (i in 0 until 100) {
                    for (j in 0 until num_simulazioni) {
                        inflazione[i][j] = inflazione_media
                    }
                }
            }

            "reale" -> {
                for (i in 0 until 100) {
                    for (j in 0 until num_simulazioni) {
                        inflazione[i][j] = inflazione_reale[Random.nextInt(inflazione_reale.size)]
                    }
                }
            }

            "reale riscalata" -> {
                val meanReale = inflazione_reale.average()
                val scaleFactor = inflazione_media / meanReale
                val inflazione_riscalata = inflazione_reale.map { it * scaleFactor }.toDoubleArray()

                for (i in 0 until 100) {
                    for (j in 0 until num_simulazioni) {
                        inflazione[i][j] = inflazione_riscalata[Random.nextInt(inflazione_riscalata.size)]
                    }
                }
            }

            "lognormale" -> {
                // Calculate variance of real inflation
                val variance = inflazione_reale.map { it * it }.average() - inflazione_reale.average().pow(2)

                // Calculate mu and sigma for lognormal distribution
                var mu = ln(inflazione_media)
                var sigma = ln((1 + sqrt(1 + 4 * variance / exp(2 * mu))) / 2)
                mu = ln(inflazione_media) - sigma.pow(2) / 2
                sigma = ln((1 + sqrt(1 + 4 * variance / exp(2 * mu))) / 2)
                mu = ln(inflazione_media) - sigma.pow(2) / 2

                // Generate lognormal random values
                for (i in 0 until 100) {
                    for (j in 0 until num_simulazioni) {
                        val z = nextGaussian()
                        inflazione[i][j] = exp(mu + sigma * z)
                    }
                }
            }

            else -> {
                println("Ciccio, guarda che non so come gestire l'inflazione!")
                for (i in 0 until 100) {
                    for (j in 0 until num_simulazioni) {
                        inflazione[i][j] = inflazione_media
                    }
                }
            }
        }

        // Calculate and print statistics
        //val flatInflazione = inflazione.flatten()
        val flatInflazione = mutableListOf<Double>()
        for (i in 0 until 100) {
            for (j in 0 until num_simulazioni) {
                flatInflazione.add(inflazione[i][j])
            }
        }
        val media = flatInflazione.average()
        val devSt = sqrt(flatInflazione.map { (it - media).pow(2) }.average())
        println("Media: $media Dev st: $devSt")

        return inflazione
    }

    fun calcolaErosioneLiquidita(liquidita_iniziale: Double, scenario_inflazione: String): Array<DoubleArray> {
        // Ottiene la matrice di inflazione dal modello scelto
        val inflazione = setInflazione(scenario_inflazione)

        // Crea una matrice per memorizzare i valori della liquidità erosa
        val liquidita_erosa = Array(100) { DoubleArray(num_simulazioni) }

        // Per ogni simulazione e anno, calcola l'erosione della liquidità
        for (j in 0 until num_simulazioni) {
            var liquidita_corrente = liquidita_iniziale
            for (i in 0 until 100) {
                // La liquidità viene erosa dall'inflazione
                liquidita_corrente /= (1 + inflazione[i][j])
                liquidita_erosa[i][j] = liquidita_corrente
            }
        }

        // Calcola e stampa alcune statistiche
        val liquiditaFinale = mutableListOf<Double>()
        for (j in 0 until num_simulazioni) {
            liquiditaFinale.add(liquidita_erosa[99][j])
        }

        val mediaFinale = liquiditaFinale.average()
        val devStFinale = sqrt(liquiditaFinale.map { (it - mediaFinale).pow(2) }.average())

        println("Statistiche finale dopo 100 anni:")
        println("Liquidita' iniziale: $liquidita_iniziale")
        println("Media liquidita' finale: $mediaFinale")
        println("Deviazione standard finale: $devStFinale")
        println("Perdita media: ${(1 - mediaFinale/liquidita_iniziale) * 100}%")

        return liquidita_erosa
    }
}

fun main() {
    val calculator = InflationCalculator(
        num_simulazioni = 1000,
        inflazione_media = 0.03  // 3%
    )

    val liquidita_iniziale = 100000.0 // 100,000 €

    // Testa tutti gli scenari
    println("\nScenario: Inflazione Reale")
    val erosioneReale = calculator.calcolaErosioneLiquidita(liquidita_iniziale, "reale")

    println("\nScenario: Inflazione Reale Riscalata")
    val erosioneRiscalata = calculator.calcolaErosioneLiquidita(liquidita_iniziale, "reale riscalata")

    println("\nScenario: Inflazione Lognormale")
    val erosioneLognormale = calculator.calcolaErosioneLiquidita(liquidita_iniziale, "lognormale")
}

//prende liquidità, per testare se funzionano i modelli --> restituisce un array con i valori della liquidità eroso
//dal % di inflazione
// Example usage:
/*fun main() {
    val calculator = InflationCalculator(
        num_simulazioni = 1000,
        inflazione_media = 0.03  // 3%
    )

    // Try different scenarios
    //val inflazioneFissa = calculator.setInflazione("fissa")
    val inflazioneReale = calculator.setInflazione("reale")
    val inflazioneRiscalata = calculator.setInflazione("reale riscalata")
    val inflazioneLognormale = calculator.setInflazione("lognormale")
}*/