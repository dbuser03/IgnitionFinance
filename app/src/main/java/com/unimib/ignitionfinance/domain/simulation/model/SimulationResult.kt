package com.unimib.ignitionfinance.domain.simulation.model

data class SimulationResult(
    val successRate: Double,
    val investedPortfolio: Array<DoubleArray>,
    val cashPortfolio: Array<DoubleArray>,
    val totalSimulations: Int,
    val simulationLength: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SimulationResult

        if (successRate != other.successRate) return false
        if (!investedPortfolio.contentDeepEquals(other.investedPortfolio)) return false
        if (!cashPortfolio.contentDeepEquals(other.cashPortfolio)) return false
        if (totalSimulations != other.totalSimulations) return false
        if (simulationLength != other.simulationLength) return false

        return true
    }

    override fun hashCode(): Int {
        var result = successRate.hashCode()
        result = 31 * result + investedPortfolio.contentDeepHashCode()
        result = 31 * result + cashPortfolio.contentDeepHashCode()
        result = 31 * result + totalSimulations
        result = 31 * result + simulationLength
        return result
    }
}