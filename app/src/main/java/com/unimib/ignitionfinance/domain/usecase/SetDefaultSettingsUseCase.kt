package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.model.Settings
import com.unimib.ignitionfinance.data.model.Withdrawals
import com.unimib.ignitionfinance.data.model.Expenses
import com.unimib.ignitionfinance.data.model.Intervals

class SetDefaultSettingsUseCase {

    fun execute(): Settings {
        return Settings(
            withdrawals = Withdrawals(
                withPension = "----",
                withoutPension = "----"
            ),
            inflationModel = "lognormal",
            expenses = Expenses(
                taxRatePercentage = "26",
                stampDutyPercentage = "0.2",
                loadPercentage = "1"
            ),
            intervals = Intervals(
                yearsInFIRE = "----",
                yearsInPaidRetirement = "----",
                yearsOfBuffer = "----"
            ),
            numberOfSimulations = "----"
        )
    }
}
