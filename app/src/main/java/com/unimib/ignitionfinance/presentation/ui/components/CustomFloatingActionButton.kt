package com.unimib.ignitionfinance.presentation.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp

@Composable
fun CustomFloatingActionButton(
    onClick: (() -> Unit) = {},
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    icon: Painter,
    contentDescription: String? = null,
    fabSize: Dp = 56.dp,
    iconSize: Dp = 24.dp,
    hapticFeedbackType: HapticFeedbackType = HapticFeedbackType.LongPress // New parameter for haptic feedback
) {
    val hapticFeedback = LocalHapticFeedback.current
    FloatingActionButton(
        onClick = {
            hapticFeedback.performHapticFeedback(hapticFeedbackType) // Use the passed feedback type
            onClick()
        },
        modifier = modifier.size(fabSize),
        containerColor = containerColor,
        contentColor = contentColor,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize)
        )
    }
}
