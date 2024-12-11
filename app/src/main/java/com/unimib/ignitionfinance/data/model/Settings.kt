package com.unimib.ignitionfinance.data.model

data class Settings (
    val withdrawals: Withdrawals,
    val inflationModel: String,
    val expenses: Expenses,
    val intervals: Intervals,
    val numberOfSimulations: String
)