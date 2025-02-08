package com.unimib.ignitionfinance.domain.simulation

import android.util.Log
import com.unimib.ignitionfinance.domain.simulation.model.SimulationConfig
import com.unimib.ignitionfinance.domain.simulation.model.SimulationResult

object FireSimulator {
    private const val TAG = "FIRE_SIMULATOR"

    fun simulatePortfolio(
        config: SimulationConfig,
        marketReturnsMatrix: Array<DoubleArray>, // matrice dei rendimenti (per "fire")
        withdrawalMatrix: Array<DoubleArray>       // matrice dei prelievi annuali (calcolata esternamente, ad es. considerando l'inflazione)
    ): SimulationResult {
        val simulationLength = 100
        val numSimulations = config.settings.numberOfSimulations.toInt()

        // Inizializzazione delle matrici per le due componenti del portafoglio:
        // "fire" = investito, "cash" = liquidità.
        val fire = Array(simulationLength) { DoubleArray(numSimulations) }
        val cash = Array(simulationLength) { DoubleArray(numSimulations) }

        // Suddivisione del capitale iniziale in base alla quota cash (ad es. 0% se tutta la somma è investita)
        val totalCapital = config.capital.invested + config.capital.cash
        val percCash = if (totalCapital > 0) config.capital.cash / totalCapital else 0.0
        for (sim in 0 until numSimulations) {
            fire[0][sim] = config.capital.invested * (1 - percCash)
            cash[0][sim] = config.capital.cash
        }

        // Il "capital load" (base d'acquisto) per il portafoglio fire, usato nel calcolo delle tasse.
        val capitalLoad = DoubleArray(numSimulations) {
            config.capital.invested * config.settings.expenses.loadPercentage.toDouble()
        }

        // Loop sugli anni (0..simulationLength-2, perché si calcola anche il passaggio all'anno successivo)
        for (t in 0 until simulationLength - 1) {
            for (sim in 0 until numSimulations) {
                // 1. Prelievo richiesto per l'anno t (valore preso dalla withdrawalMatrix, ad es. già corretta per inflazione)
                val reqWithdrawal = withdrawalMatrix[t][sim]

                // 2. Prelievo dalla componente cash (non soggetta a tasse)
                val withdrawCash = minOf(cash[t][sim], reqWithdrawal)
                val remainingReq = reqWithdrawal - withdrawCash

                // 3. Prelievo dalla componente fire per coprire l’ammontare residuo
                val withdrawFire = remainingReq

                // Calcolo della tassa solo se fire è positivo (altrimenti non è possibile prelevare)
                val tax = if (fire[t][sim] > 0)
                    withdrawFire * (1 - capitalLoad[sim] / fire[t][sim]) * config.settings.expenses.taxRatePercentage.toDouble()
                else 0.0

                // Aggiorno la componente fire: sottraggo sia il prelievo che la tassa
                // Salvo il valore precedente per aggiornare la base di carico
                val oldFire = fire[t][sim] + withdrawFire + tax
                fire[t][sim] -= (withdrawFire + tax)

                // Aggiornamento proporzionale del capital load, in analogia al Python (capitale_carico)
                capitalLoad[sim] = if (oldFire > 0) capitalLoad[sim] * (fire[t][sim] / oldFire) else 0.0

                // Aggiornamento della componente cash: sottrazione del prelievo effettuato
                cash[t][sim] -= withdrawCash

                // 4. Evoluzione del portafoglio per l'anno successivo:
                //    - fire evolve in base al rendimento di mercato
                fire[t + 1][sim] = fire[t][sim] * marketReturnsMatrix[t + 1][sim]
                //    - cash cresce al tasso cashInterestRate
                cash[t + 1][sim] = cash[t][sim] * (1 + config.simulationParams.cashInterestRate)

                // 5. Applicazione dello stamp duty sulla somma totale del portafoglio
                val totalPortfolio = fire[t + 1][sim] + cash[t + 1][sim]
                val stampDuty = totalPortfolio * config.settings.expenses.stampDutyPercentage.toDouble()
                if (totalPortfolio > 0) {
                    val fireShare = fire[t + 1][sim] / totalPortfolio
                    fire[t + 1][sim] -= stampDuty * fireShare
                    cash[t + 1][sim] -= stampDuty * (1 - fireShare)
                }
            }
        }

        // Calcolo della probabilità di successo: in quanti scenari (simulazioni) il portafoglio (fire+cash)
        // è positivo al termine del periodo FIRE (ad es. al targetYear).
        val targetYear = config.settings.intervals.yearsInFIRE.toInt()
        val successCount = (0 until numSimulations).count { sim ->
            (fire[targetYear][sim] + cash[targetYear][sim]) > 0
        }
        val successRate = (successCount.toDouble() / numSimulations) * 100

        Log.d(TAG, "Success rate: $successRate%")

        return SimulationResult(
            successRate = successRate,
            investedPortfolio = fire,
            cashPortfolio = cash,
            totalSimulations = numSimulations,
            simulationLength = simulationLength
        )
    }
}