package com.unimib.ignitionfinance.domain.usecase.settingsUseCases

import com.unimib.ignitionfinance.data.model.user.Settings
import com.unimib.ignitionfinance.data.model.user.settings.Withdrawals
import com.unimib.ignitionfinance.data.model.user.settings.Expenses
import com.unimib.ignitionfinance.data.model.user.settings.Intervals
import javax.inject.Inject

class SetDefaultSettingsUseCase @Inject constructor(){

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