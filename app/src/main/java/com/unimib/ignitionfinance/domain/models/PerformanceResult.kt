package com.unimib.ignitionfinance.domain.models

data class PerformanceResult(
    val successRate: Double,
    val mediaTotale: Double,
    val deviazioneStandardTotale: Double,
    val fuckYouMoney: Double,
    val successRateAt100k: Double,
    val successRateAt200k: Double,
    val successRateAt300k: Double
)