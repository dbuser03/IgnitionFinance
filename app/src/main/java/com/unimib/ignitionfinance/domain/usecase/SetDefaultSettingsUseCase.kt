package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.model.user.Settings
import com.unimib.ignitionfinance.data.model.user.settings.Withdrawals
import com.unimib.ignitionfinance.data.model.user.settings.Expenses
import com.unimib.ignitionfinance.data.model.user.settings.Intervals

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
