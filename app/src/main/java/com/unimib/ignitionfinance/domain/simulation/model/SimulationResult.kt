package com.unimib.ignitionfinance.domain.simulation.model

data class SimulationResult(
    val successRate: Double,

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SimulationResult

        if (successRate != other.successRate) return false


        return true
    }

    override fun hashCode(): Int {
        return successRate.hashCode()
    }


}