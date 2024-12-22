package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.unimib.ignitionfinance.domain.usecase.ResetPasswordUseCase
import com.unimib.ignitionfinance.presentation.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordScreenViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _resetState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val resetState: StateFlow<UiState<Unit>> = _resetState

    fun reset(email: String) {
        viewModelScope.launch {
            try {
                _resetState.value = UiState.Loading
                resetPasswordUseCase.execute(email).collect { result ->
                    result.fold(
                        onSuccess = { success ->
                            _resetState.value = UiState.Success(success)
                        },
                        onFailure = { throwable ->
                            _resetState.value = UiState.Error(
                                throwable.localizedMessage ?: "Reset password failed"
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                _resetState.value = UiState.Error(
                    e.localizedMessage ?: "Unexpected error occurred during password reset"
                )
            }
        }
    }
}