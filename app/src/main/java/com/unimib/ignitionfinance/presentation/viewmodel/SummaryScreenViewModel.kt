package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.domain.usecase.networth.GetUserCashUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SummaryScreenViewModel @Inject constructor (
    private val getUserCashUseCase: GetUserCashUseCase
): ViewModel() {

    private val _invested = MutableStateFlow<String?>("0")
    val invested: StateFlow<String?> = _invested

    private val _investedState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val investedState: StateFlow<UiState<String>> = _investedState

    fun getInvested() {
        viewModelScope.launch {
            _investedState.value = UiState.Loading
            getUserCashUseCase.execute()
                .collect { result ->
                    _investedState.value = when {
                        result.isSuccess -> {
                            result.getOrNull()?.let { invested ->
                                _invested.value = invested
                                UiState.Success(invested)
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