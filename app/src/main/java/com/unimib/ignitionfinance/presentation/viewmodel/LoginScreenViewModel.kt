package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.unimib.ignitionfinance.data.model.AuthData
import com.unimib.ignitionfinance.data.model.UserData
import com.unimib.ignitionfinance.domain.usecase.AddUserToDatabaseUseCase
import com.unimib.ignitionfinance.domain.usecase.DeleteAllUsersUseCase
import com.unimib.ignitionfinance.domain.usecase.LoginUserUseCase
import com.unimib.ignitionfinance.domain.usecase.SetDefaultSettingsUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.LoginScreenViewModel.StoreState
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

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val authData: AuthData) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    sealed class StoreState {
        data class Success(val successMessage: String) : StoreState()
        data class Error(val errorMessage: String) : StoreState()
    }

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            loginUserUseCase.execute(email, password).collect { result ->
                result.fold(
                    onSuccess = { authData ->
                        _loginState.value = LoginState.Success(authData)
                    },
                    onFailure = { throwable ->
                        _loginState.value = LoginState.Error(mapErrorToMessage(throwable))
                    }
                )
            }
        }
    }

    fun storeUserData(name: String, surname: String, authData: AuthData) {

        val settings = SetDefaultSettingsUseCase().execute()

        val userData = UserData(
            name = name,
            surname = surname,
            authData = authData,
            settings = settings
        )

        val collectionPath = "users"

        viewModelScope.launch {
            addUserToDatabaseUseCase.execute(collectionPath, userData).collect { result ->
                result.fold(
                    onSuccess = {
                        val successMessage = "User added to database"
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

    fun deleteAllUsers() {
        viewModelScope.launch {
            deleteAllUsersUseCase().collect { result ->
                result.fold(
                    onSuccess = {
                        val successMessage = "All user data has been deleted successfully"
                        print(StoreState.Success(successMessage))
                    },
                    onFailure = { throwable ->
                        val errorMessage = "Failed to delete user data: ${throwable.message}"
                        print(StoreState.Error(errorMessage))
                    }
                )
            }
        }
    }

    private fun mapErrorToMessage(throwable: Throwable): String {
        return when (throwable) {
            is FirebaseAuthInvalidCredentialsException -> "Invalid credentials. If you've forgotten your password, please use 'Forgot Password?' to reset it, or register a new account."
            is FirebaseAuthException -> "Authentication error: ${throwable.message}"
            else -> "Unknown error: ${throwable.localizedMessage ?: "No details available"}"
        }
    }
}