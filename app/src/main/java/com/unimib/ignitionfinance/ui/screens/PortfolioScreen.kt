package com.unimib.ignitionfinance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.ui.components.BottomNavigationBar
import com.unimib.ignitionfinance.ui.components.BottomNavigationItem
import com.unimib.ignitionfinance.ui.components.RoundedAddButton
import com.unimib.ignitionfinance.ui.components.Title
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme

@Composable
fun PortfolioScreen() {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                items = listOf(
                    BottomNavigationItem(
                        iconRes = R.drawable.outline_add_notes_24,
                        label = "Portfolio",
                        contentDescription = "Portfolio"
                    ),
                    BottomNavigationItem(
                        iconRes = R.drawable.outline_donut_large_24,
                        label = "Summary",
                        contentDescription = "Summary"
                    ),
                    BottomNavigationItem(
                        iconRes = R.drawable.outline_analytics_24,
                        label = "Simulation",
                        contentDescription = "Simulation"
                    ),
                )
            )
        }
    ) { innerPadding -> // Correctly capture innerPadding
        Column(
            modifier = Modifier
                .fillMaxSize(), // Remove innerPadding from Column
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            // Title should remain at the top without innerPadding
            Title(title = "My \nPortfolio")
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp) // Optional bottom padding for button
                    .padding(innerPadding), // Apply innerPadding only to the Box
                contentAlignment = Alignment.Center
            ) {
                RoundedAddButton(
                    icon = painterResource(id = R.drawable.outline_add_24),
                    modifier = Modifier,
                    backgroundSize = 50.dp,
                    iconSize = 28.dp
                )
            }
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
