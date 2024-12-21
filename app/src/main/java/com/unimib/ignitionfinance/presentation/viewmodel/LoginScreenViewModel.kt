package com.unimib.ignitionfinance.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.model.user.AuthData
import com.unimib.ignitionfinance.data.worker.SyncOperationScheduler
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
        object Idle : StoreState()
        object Loading : StoreState()
        data class Success(val storePair: Unit?) : StoreState()
        data class Error(val errorMessage: String) : StoreState()
    }

    sealed class DeleteState {
        object Idle : DeleteState()
        object Loading : DeleteState()
        data class Success(val deletePair: Pair<Unit?, Unit?>) : DeleteState()
        data class Error(val errorMessage: String) : DeleteState()
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
                        val errorMessage = throwable.localizedMessage ?: "No details available"
                        _loginState.value =
                            LoginState.Error(errorMessage)
                    }
                )
            }
        }
    }

    private val _storeState = MutableStateFlow<StoreState>(StoreState.Idle)

    fun storeUserData(name: String, surname: String, authData: AuthData, context: Context) {
        val settings = try {
            SetDefaultSettingsUseCase().execute()
        } catch (_: Exception) {
            _storeState.value = StoreState.Error("Failed to create default settings")
            return
        }

        val user = User(
            authData = authData,
            id = authData.id,
            name = name,
            settings = settings,
            surname = surname
        )

        viewModelScope.launch {
            try {
                _storeState.value = StoreState.Loading

                addUserToDatabaseUseCase.execute("users", user)
                    .collect { result ->
                        result.fold(
                            onSuccess = {
                                _storeState.value = StoreState.Success(it)
                                try {
                                    SyncOperationScheduler.scheduleOneTime(context)
                                } catch (_: Exception) { }
                            },
                            onFailure = { throwable ->
                                _storeState.value = StoreState.Error(throwable.localizedMessage ?: "Database operation failed")
                            }
                        )
                    }
            } catch (e: Exception) {
                _storeState.value = StoreState.Error(e.localizedMessage ?: "Unexpected error occurred")
            }
        }
    }

    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Idle)

    fun deleteAllUsers() {
        viewModelScope.launch {
            _deleteState.value = DeleteState.Loading
            deleteAllUsersUseCase.execute().collect { result ->
                result.fold(
                    onSuccess = { deletePair ->
                        _deleteState.value = DeleteState.Success(deletePair)
                    },
                    onFailure = { throwable ->
                        val errorMessage = throwable.localizedMessage ?: "No details available"
                        _deleteState.value = DeleteState.Error(errorMessage)
                    }
                )
            }
        }
    }
}