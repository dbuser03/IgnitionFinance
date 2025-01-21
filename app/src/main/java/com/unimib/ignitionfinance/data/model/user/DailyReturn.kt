package com.unimib.ignitionfinance.data.model.user

import java.math.BigDecimal


data class DailyReturn(
    val dates: String,
    val weightedReturns: BigDecimal
)
