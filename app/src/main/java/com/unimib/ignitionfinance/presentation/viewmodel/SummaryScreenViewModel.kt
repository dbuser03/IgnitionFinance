package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.domain.usecase.networth.GetUserCashUseCase
import com.unimib.ignitionfinance.domain.usecase.networth.GetUserInvestedUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SummaryScreenViewModel @Inject constructor (
    private val getUserCashUseCase: GetUserCashUseCase,
    private val getUserInvestedUseCase: GetUserInvestedUseCase
): ViewModel() {

    private val _cash = MutableStateFlow<String?>("0")
    val cash: StateFlow<String?> = _cash

    private val _cashState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val cashState: StateFlow<UiState<String>> = _cashState

    private val _invested = MutableStateFlow<String?>("0")
    val invested: StateFlow<String?> = _invested

    private val _investedState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val investedState: StateFlow<UiState<String>> = _investedState

    fun getCash() {
        viewModelScope.launch {
            _cashState.value = UiState.Loading
            getUserCashUseCase.execute()
                .collect { result ->
                    _cashState.value = when {
                        result.isSuccess -> {
                            result.getOrNull()?.let { cash ->
                                calculateNetworth()
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

    fun getInvested() {
        viewModelScope.launch {
            _investedState.value = UiState.Loading
            getUserInvestedUseCase.execute()
                .collect { result ->
                    _investedState.value = when {
                        result.isSuccess -> {
                            result.getOrNull()?.let { invested ->
                                _invested.value = invested.toString()
                                UiState.Success(invested.toString())
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

    private fun calculateNetworth() {
        val cash = _invested.value?.toDoubleOrNull() ?: 0.0
        val invested = _cash.value?.toDoubleOrNull() ?: 0.0
        val total = (cash + invested).toString()
        _cash.value = total
    }
}