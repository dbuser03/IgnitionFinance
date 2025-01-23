package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.domain.usecase.networth.GetUserInvestedUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SummaryScreenViewModel @Inject constructor (
    private val getUserInvestedUseCase: GetUserInvestedUseCase
): ViewModel() {

    private val _invested = MutableStateFlow<Double?>(0.0)
    val invested: StateFlow<Double?> = _invested

    private val _investedState = MutableStateFlow<UiState<Double>>(UiState.Loading)
    val investedState: StateFlow<UiState<Double>> = _investedState

    fun getInvested() {
        viewModelScope.launch {
            _investedState.value = UiState.Loading
            getUserInvestedUseCase.execute()
                .collect { result ->
                    _investedState.value = when {
                        result.isSuccess -> {
                            result.getOrNull()?.let { invested ->
                                _invested.value = invested
                                UiState.Success(invested)
                            } ?: UiState.Error("Invested amount not found")
                        }
                        result.isFailure -> UiState.Error(
                            result.exceptionOrNull()?.localizedMessage ?: "Failed to load invested amount"
                        )
                        else -> UiState.Idle
                    }
                }
        }
    }
}