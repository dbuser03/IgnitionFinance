package com.unimib.ignitionfinance.data.model.dataset

import java.math.BigDecimal

data class Dataset(
    val date: String,
    val weightedReturn: BigDecimal
)
