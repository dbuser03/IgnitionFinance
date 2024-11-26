package com.unimib.ignitionfinance.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.R

@Composable
fun CustomFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    val hapticFeedback = LocalHapticFeedback.current

    FloatingActionButton(
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.medium.copy(CornerSize(50)),
        elevation = FloatingActionButtonDefaults.elevation(0.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.outline_add_24),
            contentDescription = "Swipe Up",
            modifier = Modifier.size(24.dp)
        )
    }
}