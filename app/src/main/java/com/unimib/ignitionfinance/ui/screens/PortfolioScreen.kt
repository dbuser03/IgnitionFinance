package com.unimib.ignitionfinance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
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
import com.unimib.ignitionfinance.ui.components.Title
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme

@Composable
fun PortfolioScreen() {
    Scaffold(
        topBar = {
            Title(title = "My \nPortfolio")
        },
        bottomBar = {
            BottomNavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                items = listOf(
                    BottomNavigationItem(
                        iconRes = R.drawable.avd_outline_add_notes_24,
                        label = "Portfolio",
                        contentDescription = "Portfolio"
                    ),
                    BottomNavigationItem(
                        iconRes = R.drawable.avd_outline_donut_large_24,
                        label = "Summary",
                        contentDescription = "Summary"
                    ),
                    BottomNavigationItem(
                        iconRes = R.drawable.avd_outline_analytics_24,
                        label = "Simulation",
                        contentDescription = "Simulation"
                    ),
                )
            )
        },
        floatingActionButton = {
            IgnitionFinanceTheme {
                FloatingActionButton(
                    onClick = { /* Handle click action */ },
                    modifier = Modifier
                        .padding(bottom = 12.dp),  // Padding solo sul FAB
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = MaterialTheme.shapes.medium.copy(CornerSize(50)),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_add_24),
                        contentDescription = "Swipe Up",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun PortfolioScreenPreview() {
    IgnitionFinanceTheme {
        PortfolioScreen()
    }
}
