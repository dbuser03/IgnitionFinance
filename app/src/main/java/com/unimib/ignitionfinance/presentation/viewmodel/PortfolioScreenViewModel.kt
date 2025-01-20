package com.unimib.ignitionfinance.presentation.ui.screens.portfolio

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.domain.usecase.GetUserCashUseCase
import com.unimib.ignitionfinance.domain.usecase.UpdateUserCashUseCase
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

    private val _cashValue = MutableStateFlow<String>("0")
    val cashValue: StateFlow<String> = _cashValue

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        getCash()
    }

    private fun getCash() {
        viewModelScope.launch {
            _error.value = null

            getUserCashUseCase.execute()
                .catch { e ->
                    Log.e("PortfolioViewModel", "Error getting cash: ${e.message}", e)
                    _error.value = e.message
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { cash ->
                            _cashValue.value = cash
                        },
                        onFailure = { e ->
                            _error.value = e.message
                        }
                    )

                }
        }
    }

    fun updateCash(newValue: String) {
        viewModelScope.launch {
            _error.value = null

            updateUserCashUseCase.execute(newValue)
                .catch { e ->
                    Log.e("PortfolioViewModel", "Error updating cash: ${e.message}", e)
                    _error.value = e.message
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { cash ->
                            _cashValue.value = cash ?: "0"
                        },
                        onFailure = { e ->
                            _error.value = e.message
                        }
                    )

                }
        }
    }
}