package com.unimib.ignitionfinance.domain.portfolio

class PortfolioReturnsStrategy {
    fun simulaRendimenti(
        rendimentoMedio: Double,
        numSimulazioni: Int
    ): Array<DoubleArray> {
        // Simplified returns simulation based on mean return
        return Array(100) { DoubleArray(numSimulazioni) { 1 + rendimentoMedio } }
    }
}