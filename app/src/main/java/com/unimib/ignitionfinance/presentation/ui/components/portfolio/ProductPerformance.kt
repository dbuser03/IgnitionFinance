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
fun ProductPerformance(
    todayAmount: String,
    purchasedAmount: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            ProductBox(
                amount = purchasedAmount,
                currencySymbol = "$",
                alignRight = false,
                bottomLabel = "purchasedDate",
                isReadOnly = true
            )
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            ProductBox(
                amount = todayAmount,
                currencySymbol = "$",
                alignRight = true,
                bottomLabel = "TODAY",
                isReadOnly = true
            )
        }

    }
}


