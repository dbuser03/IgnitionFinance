package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.data.model.user.AuthData
import com.unimib.ignitionfinance.domain.usecase.RegisterNewUserUseCase
import com.unimib.ignitionfinance.domain.validation.RegistrationValidationResult
import com.unimib.ignitionfinance.domain.validation.RegistrationValidator
import com.unimib.ignitionfinance.presentation.viewmodel.state.RegistrationFormState
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationScreenViewModel @Inject constructor(
    private val registerNewUserUseCase: RegisterNewUserUseCase,
) : ViewModel() {

    private val _registrationState = MutableStateFlow<UiState<AuthData>>(UiState.Idle)
    val registrationState: StateFlow<UiState<AuthData>> = _registrationState

    private val _formState = MutableStateFlow(RegistrationFormState())
    val formState: StateFlow<RegistrationFormState> = _formState

    fun updateName(name: String) {
        val nameValidation = RegistrationValidator.validateName(name)
        _formState.update { currentState ->
            currentState.copy(
                name = name,
                nameError = (nameValidation as? RegistrationValidationResult.Failure)?.message,
                isValid = isFormValid(currentState.email, currentState.password, name, currentState.surname)
            )
        }
    }

    fun updateSurname(surname: String) {
        val surnameValidation = RegistrationValidator.validateSurname(surname)
        _formState.update { currentState ->
            currentState.copy(
                surname = surname,
                surnameError = (surnameValidation as? RegistrationValidationResult.Failure)?.message,
                isValid = isFormValid(currentState.email, currentState.password, currentState.name, surname)
            )
        }
    }

    fun updateEmail(email: String) {
        val emailValidation = RegistrationValidator.validateEmail(email)
        _formState.update { currentState ->
            currentState.copy(
                email = email,
                emailError = (emailValidation as? RegistrationValidationResult.Failure)?.message,
                isValid = isFormValid(email, currentState.password, currentState.name, currentState.surname)
            )
        }
    }

    fun updatePassword(password: String) {
        val passwordValidation = RegistrationValidator.validatePassword(password)
        _formState.update { currentState ->
            currentState.copy(
                password = password,
                passwordError = (passwordValidation as? RegistrationValidationResult.Failure)?.message,
                isValid = isFormValid(currentState.email, password, currentState.name, currentState.surname)
            )
        }
    }

    private fun isFormValid(email: String, password: String, name: String, surname: String): Boolean {
        return RegistrationValidator.validateRegistrationForm(name, surname, email, password) is RegistrationValidationResult.Success
    }

    fun register() {
        val currentState = _formState.value
        if (currentState.isValid) {
            viewModelScope.launch {
                try {
                    _registrationState.value = UiState.Loading
                    registerNewUserUseCase.execute(currentState.email, currentState.password).collect { result ->
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
}

