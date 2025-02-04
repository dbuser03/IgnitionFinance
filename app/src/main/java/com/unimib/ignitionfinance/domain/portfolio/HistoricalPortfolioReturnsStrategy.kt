package com.unimib.ignitionfinance.domain.portfolio

/*import com.unimib.ignitionfinance.domain.models.FireSimulationConfig
import yahoofinance.YahooFinance
import java.util.Random
import kotlin.math.min

class HistoricalPortfolioReturnsStrategy(
    private val config: FireSimulationConfig = FireSimulationConfig()
) {
    fun downloadDataset(
        datasetName: String,
        rendimentoMedio: Double = 0.10
    ): List<Double> {
        return when (datasetName) {
            "sp500" -> {
                try {
                    val stock = YahooFinance.get("^GSPC")
                    val historicalQuotes = stock.getHistory(stock.historicalQuotes.size - 1000, 253)

                    historicalQuotes.map {
                        (it.close.toDouble() - it.open.toDouble()) / it.open.toDouble()
                    }
                } catch (e: Exception) {
                    println("Error downloading SP500 data: ${e.message}")
                    List(1000) { rendimentoMedio }
                }
            }
            "fisso" -> List(1000) { rendimentoMedio }
            else -> {
                println("Ciccio, guarda che non so come gestire i dati di rendimento!")
                List(1000) { rendimentoMedio }
            }
        }
    }

    fun simulaRendimenti(
        dataset: List<Double>,
        numAnni: Int = 3
    ): Pair<Array<DoubleArray>, Array<DoubleArray>> {
        val numSimulazioni = config.numSimulazioni
        val giorniAnno = 253
        val upper = min(dataset.size - numAnni * giorniAnno - 1, dataset.size - 1)

        val md = Array(100) { DoubleArray(numSimulazioni) { 1.0 } }
        val m = Array(100) { DoubleArray(numSimulazioni) { 1.0 } }

        val random = Random()

        for (c in 0 until numSimulazioni) {
            var el = random.nextInt(upper + 1)

            for (t in 1 until 100) {
                if ((t - 1) % numAnni == 0) {
                    el = random.nextInt(upper + 1)
                }

                md[t][c] = 1 + dataset[el]
                m[t][c] = m[t - 1][c] * (1 + dataset[el])
                el += giorniAnno
            }
        }

        return Pair(m, md)
    }
}*/

//bisogna sistemare il caricamento dei dati finanziari