@file:Suppress("DEPRECATION")

package com.unimib.ignitionfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import com.unimib.ignitionfinance.ui.theme.*
import com.unimib.ignitionfinance.R
import androidx.compose.ui.res.painterResource

@Composable
fun CandlestickChartIcon(
    icon: Painter,
    modifier: Modifier = Modifier,
    backgroundSize: Dp = 40.dp,
    iconSize: Dp = 20.dp
) {
    Box(
        modifier = modifier
            .size(backgroundSize)
            .clip(shape = androidx.compose.foundation.shape.CircleShape)
            .background(color = MaterialTheme.colorScheme.onSecondary),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            contentScale = ContentScale.Fit,
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
    }
}


@Composable
fun GoBackIcon(
    icon: Painter,
    modifier: Modifier = Modifier,
    iconSize: Dp = 32.dp
) {
    Image(
        painter = icon,
        contentDescription = null,
        modifier = modifier.size(iconSize),
        contentScale = ContentScale.Fit,
        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(MaterialTheme.colorScheme.primary)
    )
}

@Preview
@Composable
fun PreviewSettingsIcon() {
    IgnitionFinanceTheme {
        CandlestickChartIcon(
            icon = painterResource(id = R.drawable.outline_candlestick_chart_24),
        )
    }
}

@Preview
@Composable
fun PreviewGoBackIcon() {
    IgnitionFinanceTheme {
        GoBackIcon(
            icon = painterResource(id = R.drawable.outline_arrow_back_24),
        )
    }
}

