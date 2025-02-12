package com.unimib.ignitionfinance.presentation.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.domain.usecase.simulation.GetLastSimulationResultUseCase
import com.unimib.ignitionfinance.domain.usecase.simulation.StartSimulationUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.state.SimulationScreenState
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SimulationScreenViewModel @Inject constructor(
    private val startSimulationUseCase: StartSimulationUseCase,
    private val getLastSimulationResultUseCase: GetLastSimulationResultUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SimulationScreenState())
    val state: StateFlow<SimulationScreenState> = _state

    init {
        getLastSimulation()
    }

    private fun getLastSimulation() {
        viewModelScope.launch {
            try {
                getLastSimulationResultUseCase.execute()
                    .collect { result ->
                        _state.update { currentState ->
                            currentState.copy(
                                lastSimulationResult = result.getOrNull(),
                                simulationState = when {
                                    result.isSuccess -> UiState.Idle
                                    else -> UiState.Error(
                                        result.exceptionOrNull()?.localizedMessage
                                            ?: "Error loading last simulation"
                                    )
                                }
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        simulationState = UiState.Error(
                            e.localizedMessage ?: "Failed to load last simulation"
                        )
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startSimulation() {
        viewModelScope.launch {
            _state.update { it.copy(simulationState = UiState.Loading) }

            try {
                startSimulationUseCase.execute()
                    .collect { result ->
                        _state.update { currentState ->
                            currentState.copy(
                                simulationState = when {
                                    result.isSuccess -> {
                                        val data = result.getOrNull()
                                        if (data != null) {
                                            UiState.Success(data)
                                        } else {
                                            UiState.Error("No data received from simulation")
                                        }
                                    }
                                    else -> UiState.Error(
                                        result.exceptionOrNull()?.localizedMessage
                                            ?: "Simulation error"
                                    )
                                },
                                lastSimulationResult = result.getOrNull()
                                    ?: currentState.lastSimulationResult
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        simulationState = UiState.Error(
                            e.localizedMessage ?: "Simulation failed"
                        )
                    )
                }
            }
        }
    }
}