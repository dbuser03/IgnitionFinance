package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.model.user.AuthData
import com.unimib.ignitionfinance.data.model.user.Settings
import com.unimib.ignitionfinance.domain.usecase.AddUserToDatabaseUseCase
import com.unimib.ignitionfinance.domain.usecase.DeleteAllUsersUseCase
import com.unimib.ignitionfinance.domain.usecase.LoginUserUseCase
import com.unimib.ignitionfinance.presentation.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val addUserToDatabaseUseCase: AddUserToDatabaseUseCase,
    private val deleteAllUsersUseCase: DeleteAllUsersUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<AuthData>>(UiState.Idle)
    val loginState: StateFlow<UiState<AuthData>> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _loginState.value = UiState.Loading
                loginUserUseCase.execute(email, password).collect { result ->
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

    private val _storeState = MutableStateFlow<UiState<Unit>>(UiState.Idle)

    fun storeUserData(name: String, surname: String, authData: AuthData, settings: Settings) {
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

                addUserToDatabaseUseCase.execute("users", user).collect { result ->
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
}

