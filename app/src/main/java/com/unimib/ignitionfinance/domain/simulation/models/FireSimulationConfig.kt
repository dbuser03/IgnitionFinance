package com.unimib.ignitionfinance.domain.simulation.models

data class FireSimulationConfig(
    val capitale: Double = 500000.0,
    val percentualeCarico: Double = 1.0,
    val prelievo: Double = 2000.0 * 13,
    val prelevoPensione: Double = 2000.0 * 13,
    val rendimentoMedio: Double = 0.08,
    val rendimentoCash: Double = 0.01,
    val bollo: Double = 0.002,
    val aliquota: Double = 0.26,
    val anniRenditaSenzaPensione: Int = 20,
    val anniRendita: Int = 20,
    val percCash: Double = 0.2,
    val percFire: Double = 0.8,
    val cashInterest: Double = 0.01,
    val numSimulazioni: Int = 1000
)