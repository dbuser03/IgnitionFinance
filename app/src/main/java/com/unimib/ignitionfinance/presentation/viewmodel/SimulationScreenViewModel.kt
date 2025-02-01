package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.domain.usecase.BuildDatasetUseCase
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
    private val getUserInvestedUseCase: GetUserInvestedUseCase,
    private val buildDatasetUseCase: BuildDatasetUseCase
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
                // Get cash amount
                val cashResult = getUserCashUseCase.execute().first()
                val cash = cashResult.getOrNull()?.toDoubleOrNull() ?: 0.0

                // Get invested amount
                val investedResult = getUserInvestedUseCase.execute().first()
                val invested = investedResult.getOrNull() ?: 0.0

                // Calculate total portfolio value
                val totalValue = cash + invested

                _state.update { currentState ->
                    currentState.copy(
                        currentPortfolioValue = totalValue,
                        portfolioValueState = UiState.Success(totalValue)
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        portfolioValueState = UiState.Error(
                            e.localizedMessage ?: "Failed to load portfolio value"
                        )
                    )
                }
            }
        }
    }

    fun startSimulation(apiKey: String) {
        viewModelScope.launch {
            _state.update { it.copy(simulationState = UiState.Loading) }

            buildDatasetUseCase.execute(apiKey)
                .catch { exception ->
                    _state.update {
                        it.copy(
                            simulationState = UiState.Error(
                                exception.localizedMessage ?: "Dataset building failed"
                            )
                        )
                    }
                }
                .collect { datasetResult ->
                    if (datasetResult.isSuccess) {
                        runSimulation()
                    } else {
                        _state.update {
                            it.copy(
                                simulationState = UiState.Error(
                                    datasetResult.exceptionOrNull()?.localizedMessage ?: "Dataset error"
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun runSimulation() {
        viewModelScope.launch {
            startSimulationUseCase.execute(
                initialInvestment = _state.value.initialInvestment,
                duration = _state.value.simulationDuration
            ).catch { exception ->
                _state.update {
                    it.copy(
                        simulationState = UiState.Error(
                            exception.localizedMessage ?: "Simulation failed"
                        )
                    )
                }
            }.collect { result ->
                _state.update { currentState ->
                    when {
                        result.isSuccess -> {
                            val simulationResult = result.getOrNull()
                            currentState.copy(
                                simulationResult = simulationResult,
                                simulationState = UiState.Success(simulationResult)
                            )
                        }
                        result.isFailure -> currentState.copy(
                            simulationState = UiState.Error(
                                result.exceptionOrNull()?.localizedMessage ?: "Simulation error"
                            )
                        )
                        else -> currentState.copy(simulationState = UiState.Idle)
                    }
                }
            }
        }
    }

    private fun getUserSettings() {
        viewModelScope.launch {
            _state.update { it.copy(parametersState = UiState.Loading) }
            getUserSettingsUseCase.execute()
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val settings = result.getOrNull()
                                currentState.copy(
                                    initialInvestment = settings?.withdrawals?.initialAmount?.toDoubleOrNull() ?: 0.0,
                                    simulationDuration = settings?.intervals?.value ?: 1,  // Assuming Intervals has a value property
                                    parametersState = UiState.Success(settings)
                                )
                            }
                            result.isFailure -> currentState.copy(
                                parametersState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to load user settings"
                                )
                            )
                            else -> currentState.copy(parametersState = UiState.Idle)
                        }
                    }
                }
        }
    }

    fun updateInitialInvestment(newValue: String) {
        val cleanValue = newValue.filter { it.isDigit() || it == '.' }
        _state.update {
            it.copy(initialInvestment = cleanValue.toDoubleOrNull() ?: 0.0)
        }
    }

    fun updateSimulationDuration(newDuration: Int) {
        _state.update {
            it.copy(simulationDuration = newDuration.coerceAtLeast(1))
        }
    }
}