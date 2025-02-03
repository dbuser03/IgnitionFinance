package com.unimib.ignitionfinance.presentation.ui.components.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.R

@Composable
fun PerformanceBox(
    leftAmount: String,
    rightAmount: String,
    modifier: Modifier = Modifier,
    leftCurrencySymbol: String = "€",
    rightCurrencySymbol: String? = null,
    leftLabel: String? = null,
    rightLabel: String? = null,
    showDivider: Boolean = true,
    onDeleteClicked: (() -> Unit)? = null
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
            .padding(top = if (showDivider) 8.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (onDeleteClicked != null) {
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
            IconButton(
                onClick = onDeleteClicked,
                modifier = Modifier.size(24.dp)
                .offset(y = 48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_delete_24),
                    contentDescription = "Delete product",
                    tint = MaterialTheme.colorScheme.primary
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
        } else {
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
}
