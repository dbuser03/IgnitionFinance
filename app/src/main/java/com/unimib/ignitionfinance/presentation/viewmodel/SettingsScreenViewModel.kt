package com.unimib.ignitionfinance.presentation.viewmodel

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel

class SettingsScreenViewModel : ViewModel() {
        var expandedCardIndex = mutableIntStateOf(-1)

        fun toggleCardExpansion(cardIndex: Int) {
            expandedCardIndex.intValue = if (expandedCardIndex.intValue == cardIndex) -1 else cardIndex
        }
}