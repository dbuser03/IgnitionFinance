package com.unimib.ignitionfinance.presentation.ui.components.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PerformanceBox(
    leftAmount: String,
    rightAmount: String,
    modifier: Modifier = Modifier,
    leftCurrencySymbol: String = "€",
    rightCurrencySymbol: String? = null,
    leftLabel: String? = null,
    rightLabel: String? = null,
    showDivider: Boolean = true
) {
    if (showDivider) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.large
                    )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.large
                    )
            )
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = if (showDivider) 8.dp else 0.dp)
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            AmountBox(
                amount = leftAmount,
                currencySymbol = leftCurrencySymbol,
                alignRight = false,
                bottomLabel = leftLabel,
                isReadOnly = true
            )
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            AmountBox(
                amount = rightAmount,
                currencySymbol = rightCurrencySymbol ?: leftCurrencySymbol,
                alignRight = true,
                bottomLabel = rightLabel,
                isReadOnly = true
            )
        }
    }
}