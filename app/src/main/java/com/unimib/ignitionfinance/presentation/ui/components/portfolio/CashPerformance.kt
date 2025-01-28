package com.unimib.ignitionfinance.presentation.ui.components.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CashPerformance(
    usdAmount: String,
    chfAmount: String,
    onUsdChanged: (String?) -> Unit,
    onChfChanged: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.secondary)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        CashBox(
            amount = usdAmount,
            onAmountChanged = onUsdChanged,
            currencySymbol = "$",
            alignRight = false,
            bottomLabel = "USD"
        )

        Spacer(modifier = Modifier.height(16.dp))

        CashBox(
            amount = chfAmount,
            onAmountChanged = onChfChanged,
            currencySymbol = "₣",
            alignRight = true,
            bottomLabel = "CHF"
        )
    }
}
