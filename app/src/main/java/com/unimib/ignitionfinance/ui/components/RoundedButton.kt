package com.unimib.ignitionfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.tooling.preview.Preview
import com.unimib.ignitionfinance.ui.theme.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon

@Composable
fun RoundedSwipeUpButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    backgroundSize: Dp = 50.dp,
    iconSize: Dp = 28.dp
) {
    IconButton(
        onClick = { /* Handle click */ },
        modifier = modifier
            .size(backgroundSize)
            .clip(shape = androidx.compose.foundation.shape.CircleShape)
            .background(PrimaryWhite),
    ) {
        Image(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            contentScale = ContentScale.Fit,
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(PrimaryBlack)
        )
    }
}

@Composable
fun RoundedSettingsButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    backgroundSize: Dp = 50.dp,
    iconSize: Dp = 28.dp
) {
    IconButton(
        onClick = { /* Handle click */ },
        modifier = modifier
            .size(backgroundSize)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(MaterialTheme.colorScheme.onSecondary)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}


@Preview
@Composable
fun PreviewRoundedSettingsButton() {
    IgnitionFinanceTheme {
        RoundedSettingsButton(
            icon = Icons.Filled.Settings,
        )
    }
}

@Preview
@Composable
fun PreviewRoundedSwipeUpButton() {
    IgnitionFinanceTheme {
        RoundedSwipeUpButton(
            icon = Icons.Filled.KeyboardArrowUp,
        )
    }
}