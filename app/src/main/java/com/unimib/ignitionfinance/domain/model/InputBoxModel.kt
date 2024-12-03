package com.unimib.ignitionfinance.domain.model

import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.TextFieldValue

data class InputBoxData(
    val label: String,
    val prefix: String = "€",
    val iconResId: Int,
    val inputValue: MutableState<TextFieldValue>
)
