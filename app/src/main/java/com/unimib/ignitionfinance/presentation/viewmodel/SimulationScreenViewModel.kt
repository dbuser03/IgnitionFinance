package com.unimib.ignitionfinance.presentation.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.domain.simulation.model.SimulationResult
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
) : ViewModel() {

    private val _state = MutableStateFlow(SimulationScreenState())
    val state: StateFlow<SimulationScreenState> = _state

    @RequiresApi(Build.VERSION_CODES.O)
    fun startSimulation(apiKey: String) {
        viewModelScope.launch {
            _state.update { it.copy(simulationState = UiState.Loading) }

            try {
                startSimulationUseCase.execute(apiKey)
                    .collect { result ->
                        _state.update { currentState ->
                            when {
                                result.isSuccess -> currentState.copy(
                                    simulationState = UiState.Success(
                                        result.getOrNull() ?: SimulationResult(
                                            successRate = 0.0,
                                            investedPortfolio = emptyArray(),
                                            cashPortfolio = emptyArray(),
                                            totalSimulations = 0,
                                            simulationLength = 0
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
            } catch (e: Exception) {
                _state.update {
                    it.copy(simulationState = UiState.Error(e.localizedMessage ?: "Simulation failed"))
                }
            }
        }
    }
}