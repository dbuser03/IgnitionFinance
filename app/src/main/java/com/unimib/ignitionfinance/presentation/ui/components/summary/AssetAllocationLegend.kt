package com.unimib.ignitionfinance.presentation.ui.components.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.presentation.ui.components.CustomIcon
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.theme.IgnitionFinanceTheme


@Composable
fun AssetAllocationLegend(
    icon: Painter,
    title: String,
    percentage: Double,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.onSecondary,
    iconColor: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomIcon(
            icon = icon,
            backgroundColor = backgroundColor,
            iconColor = iconColor
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "$title:",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = "${percentage.toInt()}%",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun AssetAllocationLegendVariants() {
    IgnitionFinanceTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssetAllocationLegend(
                icon = painterResource(id = R.drawable.outline_candlestick_chart_24),
                title = "Invested",
                percentage = 75.0,
                backgroundColor = MaterialTheme.colorScheme.primary,
                iconColor = MaterialTheme.colorScheme.onPrimary
            )
            AssetAllocationLegend(
                icon = painterResource(id = R.drawable.outline_monetization_on_24),
                title = "Cash",
                percentage = 25.0,
                backgroundColor = MaterialTheme.colorScheme.onSecondary,
                iconColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}
