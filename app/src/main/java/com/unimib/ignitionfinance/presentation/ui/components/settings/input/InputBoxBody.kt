package com.unimib.ignitionfinance.presentation.ui.components.settings.input

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun InputBoxBody(prefix: String, inputValue: String, padding: Dp = 0.dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$prefix ",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = inputValue,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
