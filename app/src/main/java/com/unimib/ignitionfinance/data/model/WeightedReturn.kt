package com.unimib.ignitionfinance.data.model

import java.math.BigDecimal

data class WeightedReturn(
    val date: String,
    val weightedReturn: BigDecimal
)
