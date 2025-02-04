package com.unimib.ignitionfinance.presentation.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.presentation.viewmodel.state.SimulationScreenState
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import com.unimib.ignitionfinance.domain.usecase.simulation.BuildDatasetOnlyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


// Updated ViewModel
@HiltViewModel
class SimulationScreenViewModel @Inject constructor(
    private val buildDatasetOnlyUseCase: BuildDatasetOnlyUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SimulationScreenState())
    val state: StateFlow<SimulationScreenState> = _state

    @RequiresApi(Build.VERSION_CODES.O)
    fun startSimulation(apiKey: String) {
        viewModelScope.launch {
            _state.update { it.copy(simulationState = UiState.Loading) }
            buildDatasetOnlyUseCase.execute(apiKey)
                .catch { exception ->
                    _state.update { it.copy(simulationState = UiState.Error(exception.localizedMessage ?: "Dataset creation failed")) }
                }
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> currentState.copy(
                                simulationState = UiState.Success(Unit)
                            )
                            else -> currentState.copy(
                                simulationState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage ?: "Dataset creation error"
                                )
                            )
                        }
                    }
                }
        }
    }
}

/*
@HiltViewModel
class SimulationScreenViewModel @Inject constructor(
    private val startSimulationUseCase: StartSimulationUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SimulationScreenState())
    val state: StateFlow<SimulationScreenState> = _state

    @RequiresApi(Build.VERSION_CODES.O)
    fun startSimulation(apiKey: String, netWorth: Double, settings: Settings) {
        viewModelScope.launch {
            _state.update { it.copy(simulationState = UiState.Loading) }
            startSimulationUseCase.execute(apiKey, netWorth, settings)
                .catch { exception ->
                    _state.update { it.copy(simulationState = UiState.Error(exception.localizedMessage ?: "Simulation failed")) }
                }
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> currentState.copy(
                                simulationState = UiState.Success(
                                    SimulationResult(
                                        finalBalance = 0.0, // Use default values as needed
                                        investmentGrowth = 0.0, // Use default values as needed
                                    )
                                )
                            )
                            else -> currentState.copy(
                                simulationState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage ?: "Simulation error"
                                )
                            )
                        }
                    }
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
*/