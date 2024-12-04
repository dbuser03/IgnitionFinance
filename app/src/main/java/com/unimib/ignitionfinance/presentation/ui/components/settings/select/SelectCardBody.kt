package com.unimib.ignitionfinance.presentation.ui.components.settings.select

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SelectCardBody(
    inputText: String,
    displayedTexts: List<String>,
    selectedText: String?,
    onTextSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            SelectBox(
                text = inputText,
                displayedTexts = displayedTexts,
                selectedText = selectedText,
                onTextSelected = onTextSelected
            )
        }
    }
}
