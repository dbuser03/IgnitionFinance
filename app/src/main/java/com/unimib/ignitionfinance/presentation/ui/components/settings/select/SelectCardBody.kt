package com.unimib.ignitionfinance.presentation.ui.components.settings.select

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SelectCardBody(
    model: SelectBoxModel,
    onTextSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            SelectBox(
                model = model,
                onTextSelected = onTextSelected
            )
        }
    }
}
