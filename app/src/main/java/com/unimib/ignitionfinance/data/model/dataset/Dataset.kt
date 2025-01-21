package com.unimib.ignitionfinance.data.model.dataset

import java.math.BigDecimal


data class Dataset(
    val dates: List<String>,
    val weightedReturns: List<BigDecimal>
) {
    init {
        require(dates.size == weightedReturns.size) { "Dates and weighted returns must have the same size" }
    }
}
