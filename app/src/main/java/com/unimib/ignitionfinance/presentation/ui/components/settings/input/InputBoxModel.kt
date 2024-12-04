package com.unimib.ignitionfinance.presentation.ui.components.settings.input

import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.TextFieldValue

data class InputBoxModel (
    val label: String,
    val prefix: String = "€",
    val iconResId: Int,
    val inputValue: MutableState<TextFieldValue>
)