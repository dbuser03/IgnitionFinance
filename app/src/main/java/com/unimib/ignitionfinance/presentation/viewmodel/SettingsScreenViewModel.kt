package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.data.model.user.Settings
import com.unimib.ignitionfinance.domain.usecase.UpdateUserSettingsUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase
) : ViewModel() {

    private val _updateState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val updateState: StateFlow<UiState<Unit>> = _updateState

    var expandedCardIndex = mutableIntStateOf(-1)
        private set

    fun updateSettings(userId: String, updatedSettings: Settings) {
        viewModelScope.launch {
            try {
                _updateState.value = UiState.Loading
                updateUserSettingsUseCase.execute(userId, updatedSettings)
                    .collect { result ->
                        _updateState.value = when {
                            result.isSuccess -> UiState.Success(Unit)
                            result.isFailure -> UiState.Error(
                                result.exceptionOrNull()?.localizedMessage
                                    ?: "Failed to update settings"
                            )
                            else -> UiState.Idle
                        }
                    }
            } catch (e: Exception) {
                _updateState.value = UiState.Error(
                    e.localizedMessage ?: "Unexpected error occurred while updating settings"
                )
            }
        }
    }

    fun toggleCardExpansion(cardIndex: Int) {
        expandedCardIndex.intValue = if (expandedCardIndex.intValue == cardIndex) -1 else cardIndex
    }
}