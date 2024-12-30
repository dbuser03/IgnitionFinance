package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.data.model.user.Settings
import com.unimib.ignitionfinance.data.model.user.settings.Expenses
import com.unimib.ignitionfinance.data.model.user.settings.Intervals
import com.unimib.ignitionfinance.data.model.user.settings.Withdrawals
import com.unimib.ignitionfinance.domain.usecase.UpdateUserSettingsUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase
) : ViewModel() {

    private val _updateState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val updateState: StateFlow<UiState<Unit>> = _updateState

    var expandedCardIndex = mutableIntStateOf(-1)
        private set

    private val _settings = mutableStateOf(
        Settings(
            withdrawals = Withdrawals("----", "----"),
            inflationModel = "SCALE",
            expenses = Expenses("26", "0.2", "1"),
            intervals = Intervals("----", "----", "----"),
            numberOfSimulations = "----"
        )
    )
    val settings: State<Settings> get() = _settings

    fun updateSettingsValue(key: String, value: String?) {
        val updatedSettings = _settings.value.copy(
            withdrawals = when (key) {
                "monthlyWithdrawalsWithoutPension" -> _settings.value.withdrawals.copy(withoutPension = value ?: _settings.value.withdrawals.withoutPension)
                "monthlyWithdrawalsWithPension" -> _settings.value.withdrawals.copy(withPension = value ?: _settings.value.withdrawals.withPension)
                else -> _settings.value.withdrawals
            },
            inflationModel = when (key) {
                "inflationModel" -> value ?: _settings.value.inflationModel
                else -> _settings.value.inflationModel
            },
            expenses = when (key) {
                "taxRatePercentage" -> _settings.value.expenses.copy(taxRatePercentage = value ?: _settings.value.expenses.taxRatePercentage)
                "stampDutyPercentage" -> _settings.value.expenses.copy(stampDutyPercentage = value ?: _settings.value.expenses.stampDutyPercentage)
                "loadPercentage" -> _settings.value.expenses.copy(loadPercentage = value ?: _settings.value.expenses.loadPercentage)
                else -> _settings.value.expenses
            },
            intervals = when (key) {
                "yearsInFire" -> _settings.value.intervals.copy(yearsInFIRE = value ?: _settings.value.intervals.yearsInFIRE)
                "yearsInPaidRetirement" -> _settings.value.intervals.copy(yearsInPaidRetirement = value ?: _settings.value.intervals.yearsInPaidRetirement)
                "yearsOfBuffer" -> _settings.value.intervals.copy(yearsOfBuffer = value ?: _settings.value.intervals.yearsOfBuffer)
                else -> _settings.value.intervals
            },
            numberOfSimulations = when (key) {
                "numberOfSimulations" -> value ?: _settings.value.numberOfSimulations
                else -> _settings.value.numberOfSimulations
            }
        )
        _settings.value = updatedSettings
        updateUserSettings(updatedSettings)
    }

    private fun updateUserSettings(updatedSettings: Settings) {
        viewModelScope.launch {
            updateUserSettingsUseCase.execute(updatedSettings)
                .collect { result ->
                    _updateState.value = when {
                        result.isSuccess -> UiState.Success(Unit)
                        result.isFailure -> UiState.Error(
                            result.exceptionOrNull()?.localizedMessage
                                ?: "Failed to update settings"
                        )
                        else -> UiState.Idle
                    }
                }
        }
    }

    fun toggleCardExpansion(cardIndex: Int) {
        expandedCardIndex.intValue = if (expandedCardIndex.intValue == cardIndex) -1 else cardIndex
    }
}