package com.unimib.ignitionfinance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.ui.components.RoundedAddButton
import com.unimib.ignitionfinance.ui.components.Title
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme

@Composable
fun PortfolioScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Title(title = "My \nPortfolio")
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 120.dp),
            contentAlignment = Alignment.Center
        ) {
            RoundedAddButton(
                icon = Icons.Filled.Add,
                modifier = Modifier,
                backgroundSize = 50.dp,
                iconSize = 28.dp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PortfolioScreenPreview() {
    IgnitionFinanceTheme {
        PortfolioScreen()
    }
}