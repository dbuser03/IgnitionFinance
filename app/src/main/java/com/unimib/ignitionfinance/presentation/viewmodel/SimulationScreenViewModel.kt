package com.unimib.ignitionfinance.presentation.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.domain.usecase.StartSimulationUseCase
import com.unimib.ignitionfinance.domain.usecase.settings.GetUserSettingsUseCase
import com.unimib.ignitionfinance.domain.usecase.networth.GetUserCashUseCase
import com.unimib.ignitionfinance.domain.usecase.networth.GetUserInvestedUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.state.SimulationScreenState
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SimulationScreenViewModel @Inject constructor(
    private val startSimulationUseCase: StartSimulationUseCase,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val getUserCashUseCase: GetUserCashUseCase,
    private val getUserInvestedUseCase: GetUserInvestedUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SimulationScreenState())
    val state: StateFlow<SimulationScreenState> = _state

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        getPortfolioValue()
        getUserSettings()
    }

    private fun getPortfolioValue() {
        viewModelScope.launch {
            _state.update { it.copy(portfolioValueState = UiState.Loading) }
            try {
                val cash = getUserCashUseCase.execute().first().getOrNull()?.toDoubleOrNull() ?: 0.0
                val invested = getUserInvestedUseCase.execute().first().getOrNull() ?: 0.0
                _state.update { it.copy(currentPortfolioValue = cash + invested, portfolioValueState = UiState.Success(cash + invested)) }
            } catch (e: Exception) {
                _state.update { it.copy(portfolioValueState = UiState.Error(e.localizedMessage ?: "Failed to load portfolio value")) }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startSimulation(apiKey: String) {
        viewModelScope.launch {
            _state.update { it.copy(simulationState = UiState.Loading) }
            startSimulationUseCase.execute(apiKey)
                .catch { exception ->
                    _state.update { it.copy(simulationState = UiState.Error(exception.localizedMessage ?: "Simulation failed")) }
                }
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> currentState.copy(simulationState = UiState.Success(Unit))
                            else -> currentState.copy(simulationState = UiState.Error(result.exceptionOrNull()?.localizedMessage ?: "Simulation error"))
                        }
                    }
                }
        }
    }

    private fun getUserSettings() {
        viewModelScope.launch {
            _state.update { it.copy(parametersState = UiState.Loading) }
            getUserSettingsUseCase.execute()
                .catch { exception ->
                    _state.update { it.copy(parametersState = UiState.Error(exception.localizedMessage ?: "Failed to load user settings")) }
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { settings ->
                            _state.update {
                                it.copy(
                                    initialInvestment = settings.withdrawals.withPension.toDoubleOrNull() ?: 0.0,
                                    simulationDuration = settings.intervals.yearsInFIRE.toIntOrNull() ?: 1,
                                    parametersState = UiState.Success(settings)
                                )
                            }
                        },
                        onFailure = { exception ->
                            _state.update { it.copy(parametersState = UiState.Error(exception.localizedMessage ?: "Failed to load user settings")) }
                        }
                    )
                }
        }
    }

    fun updateInitialInvestment(newValue: String) {
        val cleanValue = newValue.filter { it.isDigit() || it == '.' }
        _state.update { it.copy(initialInvestment = cleanValue.toDoubleOrNull() ?: 0.0) }
    }

    fun updateSimulationDuration(newDuration: Int) {
        _state.update { it.copy(simulationDuration = newDuration.coerceAtLeast(1)) }
    }
}
