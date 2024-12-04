package com.unimib.ignitionfinance.presentation.ui.components.title

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun TitleText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    CustomText(
        text = text,
        color = color,
        style = MaterialTheme.typography.headlineLarge,
        modifier = modifier,
        maxLines = 2
    )
}
