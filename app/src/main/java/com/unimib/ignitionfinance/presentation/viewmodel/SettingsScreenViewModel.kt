package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.data.model.user.Settings
import com.unimib.ignitionfinance.domain.usecase.GetUserSettingsUseCase
import com.unimib.ignitionfinance.domain.usecase.SetDefaultSettingsUseCase
import com.unimib.ignitionfinance.domain.usecase.UpdateUserSettingsUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log
import kotlinx.coroutines.flow.catch

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val setDefaultSettingsUseCase: SetDefaultSettingsUseCase
) : ViewModel() {
    private val _updateState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val updateState: StateFlow<UiState<Unit>> = _updateState

    private val _settingsState = MutableStateFlow<UiState<Settings>>(UiState.Loading)
    val settingsState: StateFlow<UiState<Settings>> = _settingsState

    private val _settings = mutableStateOf<Settings?>(null)
    val settings: State<Settings?> = _settings

    var expandedCardIndex = mutableIntStateOf(-1)
        private set

    fun getDefaultSettings(): Settings {
        return setDefaultSettingsUseCase.execute()
    }

    fun getUserSettings() {
        viewModelScope.launch {
            _settingsState.value = UiState.Loading
            getUserSettingsUseCase.execute()
                .collect { result ->
                    _settingsState.value = when {
                        result.isSuccess -> {
                            result.getOrNull()?.let { settings ->
                                _settings.value = settings
                                UiState.Success(settings)
                            } ?: UiState.Error("Settings not found")
                        }
                        result.isFailure -> UiState.Error(
                            result.exceptionOrNull()?.localizedMessage ?: "Failed to load settings"
                        )
                        else -> UiState.Idle
                    }
                }
        }
    }

    fun updateSettings(newSettings: Settings) {
        viewModelScope.launch {
            _updateState.value = UiState.Loading
            Log.d("SettingsScreenViewModel", "Updating settings: $newSettings")
            updateUserSettingsUseCase.execute(newSettings)
                .catch { exception ->
                    Log.e("SettingsScreenViewModel", "Update failed: ${exception.localizedMessage}")
                    _updateState.value = UiState.Error(
                        exception.localizedMessage ?: "Failed to update settings"
                    )
                }
                .collect { result ->
                    Log.d("SettingsScreenViewModel", "Update result: $result")
                    _updateState.value = when {
                        result.isSuccess -> {
                            result.getOrNull()?.let { settings ->
                                _settings.value = settings
                                Log.d("SettingsScreenViewModel", "Settings updated successfully: $settings")
                                UiState.Success(Unit)
                            } ?: UiState.Error("Failed to update settings")
                        }
                        result.isFailure -> {
                            UiState.Error(
                                result.exceptionOrNull()?.localizedMessage ?: "Failed to update settings"
                            )
                        }
                        else -> UiState.Idle
                    }
                }
        }
    }

    fun toggleCardExpansion(cardIndex: Int) {
        expandedCardIndex.intValue = if (expandedCardIndex.intValue == cardIndex) -1 else cardIndex
    }
}