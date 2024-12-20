package com.unimib.ignitionfinance.presentation.viewmodel

import android.content.Context
import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

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
    val storeState: StateFlow<StoreState> = _storeState.asStateFlow()
    private val TAG = "IgnitionFinance_UserViewModel"

    fun storeUserData(name: String, surname: String, authData: AuthData, context: Context) {
        Log.d(TAG, "Starting storeUserData for user: ${authData.id}")

        val settings = SetDefaultSettingsUseCase().execute()
        Log.d(TAG, "Default settings created")

        val user = User(
            authData = authData,
            id = authData.id,
            name = name,
            settings = settings,
            surname = surname
        )
        Log.d(TAG, "User object created: $user")

        val collectionPath = "users"

        viewModelScope.launch {
            try {
                Log.d(TAG, "Launching coroutine for database operations")
                _storeState.value = StoreState.Loading

                withContext(Dispatchers.IO) {
                    val result = addUserToDatabaseUseCase.execute(collectionPath, user)

                    result.fold(
                        onSuccess = {
                            Log.d(TAG, "Database operation successful, scheduling sync")
                            _storeState.value = StoreState.Success(Unit)
                            try {
                                Log.d(TAG, "Attempting to schedule one-time sync")
                                SyncOperationScheduler.scheduleOneTime(context)
                                Log.d(TAG, "Sync scheduled successfully")
                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to schedule sync", e)
                            }
                        },
                        onFailure = { throwable ->
                            val errorMessage = throwable.localizedMessage ?: "No details available"
                            Log.e(TAG, "Database operation failed: $errorMessage", throwable)
                            _storeState.value = StoreState.Error(errorMessage)
                        }
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Error in coroutine scope", e)
                _storeState.value = StoreState.Error(e.localizedMessage ?: "Error in coroutine scope")
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