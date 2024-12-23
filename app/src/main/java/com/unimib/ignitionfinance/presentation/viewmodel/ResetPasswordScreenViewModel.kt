package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.domain.usecase.ResetPasswordUseCase
import com.unimib.ignitionfinance.domain.validation.ResetValidationResult
import com.unimib.ignitionfinance.domain.validation.ResetValidator
import com.unimib.ignitionfinance.presentation.viewmodel.state.ResetPasswordFormState
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordScreenViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _resetState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val resetState: StateFlow<UiState<Unit>> = _resetState

    private val _formState = MutableStateFlow(ResetPasswordFormState())
    val formState: StateFlow<ResetPasswordFormState> = _formState

    fun updateEmail(email: String) {
        val emailValidation = ResetValidator.validateEmail(email)
        _formState.update { currentState ->
            currentState.copy(
                email = email,
                emailError = (emailValidation as? ResetValidationResult.Failure)?.message,
                isValid = isFormValid(email)
            )
        }
    }

    private fun isFormValid(email: String): Boolean {
        return ResetValidator.validateResetForm(email) is ResetValidationResult.Success
    }

    fun reset() {
        val currentState = _formState.value
        if (currentState.isValid) {
            viewModelScope.launch {
                try {
                    _resetState.value = UiState.Loading
                    resetPasswordUseCase.execute(currentState.email).collect { result ->
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
}