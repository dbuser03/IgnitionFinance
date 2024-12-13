package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.domain.usecase.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordScreenViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    sealed class ResetState {
        data object Idle : ResetState() //
        data object Loading : ResetState() //
        data class Success(val successMessage: String) : ResetState()
        data class Error(val errorMessage: String) : ResetState()
    }

    private val _resetState = MutableStateFlow<ResetState>(ResetState.Idle) //

    val resetState: StateFlow<ResetState> = _resetState //

    fun reset(email: String) {
        viewModelScope.launch {
            _resetState.value = ResetState.Loading //
            resetPasswordUseCase.execute(email).collect { result ->
                result.fold(
                    onSuccess = {
                        val successMessage = "Password reset successful"
                        _resetState.value = ResetState.Success(successMessage)
                    },
                    onFailure = {
                        val errorMessage = "Password reset failed"
                        _resetState.value = ResetState.Error(errorMessage)
                    }
                )
            }
        }
    }
}
