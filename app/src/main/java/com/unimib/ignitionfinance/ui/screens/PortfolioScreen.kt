package com.unimib.ignitionfinance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.unimib.ignitionfinance.ui.components.Title
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme

@Composable
fun PortfolioScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Title(
            title = "Your \nPortfolio"
        )

        Box(modifier = Modifier.weight(1f)) {

        }
    }
}

@Composable
@Preview(showBackground = true)
fun PortfolioScreenPreview() {
    IgnitionFinanceTheme {
        PortfolioScreen()
    }
}