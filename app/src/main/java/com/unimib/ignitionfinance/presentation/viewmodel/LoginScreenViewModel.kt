package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.model.user.AuthData
import com.unimib.ignitionfinance.data.model.user.Settings
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.domain.usecase.*
import com.unimib.ignitionfinance.domain.validation.LoginValidationResult
import com.unimib.ignitionfinance.domain.validation.LoginValidator
import com.unimib.ignitionfinance.presentation.viewmodel.state.LoginFormState
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val addUserToDatabaseUseCase: AddUserToDatabaseUseCase,
    private val deleteAllUsersUseCase: DeleteAllUsersUseCase,
    val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<AuthData>>(UiState.Idle)
    val loginState: StateFlow<UiState<AuthData>> = _loginState

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState

    fun updateEmail(email: String) {
        val emailValidation = LoginValidator.validateEmail(email)
        _formState.update { currentState ->
            currentState.copy(
                email = email,
                emailError = (emailValidation as? LoginValidationResult.Failure)?.message,
                isValid = isFormValid(email, currentState.password)
            )
        }
    }

    fun updatePassword(password: String) {
        val passwordValidation = LoginValidator.validatePassword(password)
        _formState.update { currentState ->
            currentState.copy(
                password = password,
                passwordError = (passwordValidation as? LoginValidationResult.Failure)?.message,
                isValid = isFormValid(currentState.email, password)
            )
        }
    }

    private fun isFormValid(email: String, password: String): Boolean {
        return LoginValidator.validateLoginForm(email, password) is LoginValidationResult.Success
    }

    fun login() {
        val currentState = _formState.value
        if (currentState.isValid) {
            viewModelScope.launch {
                try {
                    _loginState.value = UiState.Loading
                    loginUserUseCase.execute(currentState.email, currentState.password).collect { result ->
                        result.fold(
                            onSuccess = { authData ->
                                _loginState.value = UiState.Success(authData)
                            },
                            onFailure = { throwable ->
                                _loginState.value = UiState.Error(
                                    throwable.localizedMessage ?: "No details available"
                                )
                            }
                        )
                    }
                } catch (e: Exception) {
                    _loginState.value = UiState.Error(
                        e.localizedMessage ?: "Unexpected error occurred during login"
                    )
                }
            }
        }
    }

    private val _storeState = MutableStateFlow<UiState<Unit>>(UiState.Idle)

    private fun storeUserDataRemote(name: String, surname: String, authData: AuthData, settings: Settings) {
        viewModelScope.launch {
            try {
                _storeState.value = UiState.Loading

                val user = User(
                    authData = authData,
                    id = authData.id,
                    name = name,
                    settings = settings,
                    surname = surname
                )

                addUserToDatabaseUseCase.executeNewUser("users", user).collect { result ->
                    result.fold(
                        onSuccess = {
                            _storeState.value = UiState.Success(Unit)
                        },
                        onFailure = { throwable ->
                            _storeState.value = UiState.Error(
                                throwable.localizedMessage ?: "Database operation failed"
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                _storeState.value = UiState.Error(
                    e.localizedMessage ?: "Unexpected error occurred during user storage"
                )
            }
        }
    }

    private fun storeUserDataLocal(user: Map<String, Any>?) {
        viewModelScope.launch {
            try {
                _storeState.value = UiState.Loading

                addUserToDatabaseUseCase.executeExistingUser(user).collect { result ->
                    result.fold(
                        onSuccess = {
                            _storeState.value = UiState.Success(Unit)
                        },
                        onFailure = { throwable ->
                            _storeState.value = UiState.Error(
                                throwable.localizedMessage ?: "Database operation failed"
                            )
                        }
                    )

                }
            } catch (e: Exception) {
                _storeState.value = UiState.Error(
                    e.localizedMessage ?: "Unexpected error occurred during user storage"
                )
            }
        }
    }

    private val _deleteState = MutableStateFlow<UiState<Pair<Unit?, Unit?>>>(UiState.Idle)

    fun deleteAllUsers() {
        viewModelScope.launch {
            try {
                _deleteState.value = UiState.Loading
                deleteAllUsersUseCase.execute().collect { result ->
                    result.fold(
                        onSuccess = { deletePair ->
                            _deleteState.value = UiState.Success(deletePair)
                        },
                        onFailure = { throwable ->
                            _deleteState.value = UiState.Error(
                                throwable.localizedMessage ?: "No details available"
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                _deleteState.value = UiState.Error(
                    e.localizedMessage ?: "Unexpected error occurred during deletion"
                )
            }
        }
    }

    fun handleLoginState(
        loginState: UiState<AuthData>,
        name: String,
        surname: String,
        onNavigateToPortfolio: () -> Unit
    ) {
        viewModelScope.launch {
            when (loginState) {
                is UiState.Success -> {
                    val authData = loginState.data

                    val result = firestoreRepository.getDocumentById("users", authData.id).firstOrNull()

                    if (result?.getOrNull() != null) {
                        storeUserDataLocal(result.getOrNull())
                    } else {
                        val settings = try {
                            SetDefaultSettingsUseCase().execute()
                        } catch (_: Exception) {
                            return@launch
                        }

                        storeUserDataRemote(
                            name = name,
                            surname = surname,
                            authData = authData,
                            settings = settings
                        )
                    }
                    onNavigateToPortfolio()
                }
                is UiState.Error -> {
                    val errorMessage = loginState.message
                    println("Login error: $errorMessage")
                }
                else -> {}
            }
        }
    }
}

