package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.domain.usecase.RetrieveUserSettingsUseCase
import com.unimib.ignitionfinance.presentation.model.SelectBoxModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val retrieveUserSettingsUseCase: RetrieveUserSettingsUseCase
) : ViewModel() {
    var expandedCardIndex = mutableIntStateOf(-1)

    var withdrawalsWithoutPension = mutableStateOf("")
    var withdrawalsWithPension = mutableStateOf("")
    var taxRatePercentage = mutableStateOf("")
    var stampDutyPercentage = mutableStateOf("")
    var loadPercentage = mutableStateOf("")
    var yearsInFIRE = mutableStateOf("")
    var yearsInPaidRetirement = mutableStateOf("")
    var yearsOfBuffer = mutableStateOf("")
    var inflationModel = SelectBoxModel("", listOf())
    var numberOfSimulations = mutableStateOf("")

    init {
        retrieveUserSettings()
    }

    fun toggleCardExpansion(cardIndex: Int) {
        expandedCardIndex.intValue = if (expandedCardIndex.intValue == cardIndex) -1 else cardIndex
    }

    private fun retrieveUserSettings() {
        retrieveUserSettingsUseCase.execute().onEach { result ->
            result.onSuccess { settings ->
                withdrawalsWithoutPension.value = settings.withdrawals.withoutPension
                withdrawalsWithPension.value = settings.withdrawals.withPension
                taxRatePercentage.value = settings.expenses.taxRatePercentage
                stampDutyPercentage.value = settings.expenses.stampDutyPercentage
                loadPercentage.value = settings.expenses.loadPercentage
                yearsInFIRE.value = settings.intervals.yearsInFIRE
                yearsInPaidRetirement.value = settings.intervals.yearsInPaidRetirement
                yearsOfBuffer.value = settings.intervals.yearsOfBuffer
                inflationModel.selectedText = settings.inflationModel
                numberOfSimulations.value = settings.numberOfSimulations
            }
            result.onFailure { exception ->
                println("Error retrieving user settings: ${exception.message}")
            }
        }.launchIn(viewModelScope)
    }
}