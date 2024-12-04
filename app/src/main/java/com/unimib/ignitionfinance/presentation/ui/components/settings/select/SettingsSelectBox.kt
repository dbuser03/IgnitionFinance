package com.unimib.ignitionfinance.presentation.ui.components.settings.select

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsSelectBox(
    text: String,
    displayedTexts: List<String>,
    selectedText: String? = null,
    onTextSelected: (String) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(152.dp) // Consider moving this value to a theme or constant
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) { }
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.TopStart)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .padding(top = 24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            displayedTexts.forEach { displayedText ->
                SelectableRow(
                    text = displayedText,
                    isSelected = selectedText == displayedText,
                    onClick = { onTextSelected(displayedText) }
                )
            }
        }
    }
}
