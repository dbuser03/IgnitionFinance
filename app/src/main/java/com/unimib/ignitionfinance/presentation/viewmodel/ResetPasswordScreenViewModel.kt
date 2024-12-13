package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.domain.usecase.ResetPasswordUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.LoginScreenViewModel.StoreState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class ResetPasswordScreenViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    sealed class ResetState {
        data class Success(val successMessage: String) : ResetState()
        data class Error(val errorMessage: String) : ResetState()
    }

    fun reset(email: String) {
        viewModelScope.launch {
           resetPasswordUseCase.execute(email).collect { result ->
                result.fold(
                    onSuccess = {
                        val successMessage = "Password reset"
                        print(StoreState.Success(successMessage))
                    },
                    onFailure = {
                        val errorMessage = "Failure"
                        print(StoreState.Error(errorMessage))
                    }
                )
            }
        }
    }

}