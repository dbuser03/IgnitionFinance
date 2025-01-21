package com.unimib.ignitionfinance.presentation.ui.screens.portfolio

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.domain.usecase.cash.GetUserCashUseCase
import com.unimib.ignitionfinance.domain.usecase.cash.UpdateUserCashUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioScreenViewModel @Inject constructor(
    private val getUserCashUseCase: GetUserCashUseCase,
    private val updateUserCashUseCase: UpdateUserCashUseCase
) : ViewModel() {

    private val _cash = MutableStateFlow<String?>("0")
    val cash: StateFlow<String?> = _cash

    private val _cashState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val cashState: StateFlow<UiState<String>> = _cashState

    private fun getCash() {
        viewModelScope.launch {
            _cashState.value = UiState.Loading
            getUserCashUseCase.execute()
                .collect { result ->
                    _cashState.value = when {
                        result.isSuccess -> {
                            result.getOrNull()?.let { cash ->
                                _cash.value = cash
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

    fun updateCash(newCash: String) {
        viewModelScope.launch {
            _cashState.value = UiState.Loading

            updateUserCashUseCase.execute(newCash)
                .catch { exception ->
                    Log.e("PortfolioViewModel", "Error updating cash: ${exception.localizedMessage}")
                    _cashState.value = UiState.Error(
                        exception.localizedMessage ?: "Failed to update cash"
                    )
                }
                .collect { result ->
                    _cashState.value = when {
                        result.isSuccess -> {
                            result.getOrNull()?.let { cash ->
                                _cash.value = cash
                                UiState.Success(cash)
                            } ?: UiState.Error("Failed to update cash")
                        }
                        result.isFailure -> {
                            UiState.Error(
                                result.exceptionOrNull()?.localizedMessage ?: "Failed to update cash"
                            )
                        }
                        else -> UiState.Idle
                    }

                }
        }
    }
}