package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.data.model.user.AuthData
import com.unimib.ignitionfinance.domain.usecase.RegisterNewUserUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationScreenViewModel @Inject constructor(
    private val registerNewUserUseCase: RegisterNewUserUseCase,
) : ViewModel() {

    private val _registrationState = MutableStateFlow<UiState<AuthData>>(UiState.Idle)
    val registrationState: StateFlow<UiState<AuthData>> = _registrationState

    fun register(email: String, password: String) {
        viewModelScope.launch {
            try {
                _registrationState.value = UiState.Loading
                registerNewUserUseCase.execute(email, password).collect { result ->
                    result.fold(
                        onSuccess = { authData ->
                            _registrationState.value = UiState.Success(authData)
                        },
                        onFailure = { throwable ->
                            _registrationState.value = UiState.Error(
                                throwable.localizedMessage ?: "Registration failed"
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                _registrationState.value = UiState.Error(
                    e.localizedMessage ?: "Unexpected error occurred during registration"
                )
            }
        }
    }
}

