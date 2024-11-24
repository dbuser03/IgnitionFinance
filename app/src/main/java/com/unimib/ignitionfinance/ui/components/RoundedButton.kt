package com.unimib.ignitionfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.tooling.preview.Preview
import com.unimib.ignitionfinance.ui.theme.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.unimib.ignitionfinance.R

@Composable
fun RoundedSwipeUpButton(
    icon: Painter,
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
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            contentScale = ContentScale.Fit,
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(PrimaryBlack)
        )
    }
}

@Composable
fun RoundedSettingsButton(
    icon: Painter,
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
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun RoundedAddButton(
    icon: Painter,
    modifier: Modifier = Modifier,
    backgroundSize: Dp = 50.dp,
    iconSize: Dp = 28.dp
) {
    IconButton(
        onClick = { /* Handle click */ },
        modifier = modifier
            .size(backgroundSize)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}


@Preview
@Composable
fun PreviewRoundedSettingsButton() {
    IgnitionFinanceTheme {
        RoundedSettingsButton(
            icon = painterResource(id = R.drawable.outline_settings_24),
        )
    }
}

@Preview
@Composable
fun PreviewRoundedSwipeUpButton() {
    IgnitionFinanceTheme {
        RoundedSwipeUpButton(
            icon = painterResource(id = R.drawable.outline_keyboard_arrow_up_24),
        )
    }
}

@Preview
@Composable
fun PreviewRoundedAddButton() {
    IgnitionFinanceTheme {
        RoundedAddButton(
            icon = painterResource(id = R.drawable.outline_add_24),
        )
    }
}