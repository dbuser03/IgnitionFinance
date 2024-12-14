package com.unimib.ignitionfinance.data.model.user

import com.unimib.ignitionfinance.data.model.user.settings.Expenses
import com.unimib.ignitionfinance.data.model.user.settings.Intervals
import com.unimib.ignitionfinance.data.model.user.settings.Withdrawals

data class Settings (
    val withdrawals: Withdrawals,
    val inflationModel: String,
    val expenses: Expenses,
    val intervals: Intervals,
    val numberOfSimulations: String
)