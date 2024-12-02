package com.unimib.ignitionfinance.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme
import com.unimib.ignitionfinance.R

@Composable
fun IconWithBackground(
    icon: Painter,
    modifier: Modifier = Modifier,
    backgroundSize: Dp = 40.dp,
    iconSize: Dp = 20.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.onSecondary
) {
    Box(
        modifier = modifier
            .size(backgroundSize)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            contentScale = androidx.compose.ui.layout.ContentScale.Fit,
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
    }
}

@Preview
@Composable
fun PreviewSettingsIcon() {
    IgnitionFinanceTheme {
        IconWithBackground(
            icon = painterResource(id = R.drawable.outline_candlestick_chart_24)
        )
    }
}

@Preview
@Composable
fun PreviewPensionIcon() {
    IgnitionFinanceTheme {
        IconWithBackground(
            icon = painterResource(id = R.drawable.outline_person_4_24)
        )
    }
}

@Preview
@Composable
fun PreviewNoPensionIcon() {
    IgnitionFinanceTheme {
        IconWithBackground(
            icon = painterResource(id = R.drawable.outline_person_apron_24)
        )
    }
}

@Preview
@Composable
fun PreviewSelectedIcon() {
    IgnitionFinanceTheme {
        IconWithBackground(
            icon = painterResource(id = R.drawable.outline_check_24)
        )
    }
}