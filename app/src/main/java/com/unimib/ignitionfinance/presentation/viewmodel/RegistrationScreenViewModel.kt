package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.data.model.AuthData
import com.unimib.ignitionfinance.domain.usecase.RegisterNewUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationScreenViewModel @Inject constructor(
    private val registerNewUserUseCase: RegisterNewUserUseCase
) : ViewModel() {

    sealed class RegistrationState {
        object Idle : RegistrationState()
        object Loading : RegistrationState()
        data class Success(val authData: AuthData) : RegistrationState()
        data class Error(val message: String) : RegistrationState()
    }

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            registerNewUserUseCase.execute(email, password).collect { result ->
                result.fold(
                    onSuccess = { authData ->
                        _registrationState.value = RegistrationState.Success(authData)
                    },
                    onFailure = { throwable ->
                        _registrationState.value = RegistrationState.Error(throwable.message ?: "Unknown error")
                    }
                )
            }
        }
    }
}
