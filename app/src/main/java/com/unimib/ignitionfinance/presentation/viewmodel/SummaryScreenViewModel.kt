package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.domain.usecase.networth.GetUserInvestedUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.state.SummaryScreenState
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SummaryScreenViewModel @Inject constructor(
    private val getUserInvestedUseCase: GetUserInvestedUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SummaryScreenState())
    val state: StateFlow<SummaryScreenState> = _state

    fun toggleNetWorthVisibility() {
        _state.update { currentState ->
            currentState.copy(isNetWorthHidden = !currentState.isNetWorthHidden)
        }
    }

    fun getInvested() {
        viewModelScope.launch {
            _state.update { it.copy(investedState = UiState.Loading) }
            getUserInvestedUseCase.execute()
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val invested = result.getOrNull()
                                if (invested != null) {
                                    currentState.copy(
                                        invested = invested,
                                        investedState = UiState.Success(invested)
                                    )
                                } else {
                                    currentState.copy(
                                        investedState = UiState.Error("Invested amount not found")
                                    )
                                }
                            }
                            result.isFailure -> currentState.copy(
                                investedState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to load invested amount"
                                )
                            )
                            else -> currentState.copy(investedState = UiState.Idle)
                        }
                    }
                }
        }
    }
}