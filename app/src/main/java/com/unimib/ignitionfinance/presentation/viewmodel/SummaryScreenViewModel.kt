package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SummaryScreenViewModel {
    private val _invested = MutableStateFlow<String?>("0")
    val cash: StateFlow<String?> = _invested

    private val _investedState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val cashState: StateFlow<UiState<String>> = _investedState

    fun getCash() {
        viewModelScope.launch {
            _investedState.value = UiState.Loading
            getUserCashUseCase.execute()
                .collect { result ->
                    _cashState.value = when {
                        result.isSuccess -> {
                            result.getOrNull()?.let { cash ->
                                _invested.value = cash
                                UiState.Success(cash)
                            } ?: UiState.Error("Cash not found")
                        }
                        result.isFailure -> UiState.Error(
                            result.exceptionOrNull()?.localizedMessage ?: "Failed to load cash"
                        )
                        else -> UiState.Idle
                    }
                }
        }
    }
}