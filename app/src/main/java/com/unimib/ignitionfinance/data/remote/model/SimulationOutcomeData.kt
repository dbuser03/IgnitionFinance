package com.unimib.ignitionfinance.data.remote.model

import com.unimib.ignitionfinance.domain.simulation.model.SimulationResult

data class SimulationOutcomeData(
    val results: List<SimulationResult>,
    val fuckYouMoney: Double
)
