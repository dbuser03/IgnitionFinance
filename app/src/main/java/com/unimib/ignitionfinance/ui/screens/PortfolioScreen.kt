package com.unimib.ignitionfinance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.ui.components.BottomNavigationBar
import com.unimib.ignitionfinance.ui.components.BottomNavigationItem
import com.unimib.ignitionfinance.ui.components.CustomFloatingActionButton
import com.unimib.ignitionfinance.ui.components.Title
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme

@Composable
fun PortfolioScreen(navController: NavController) {
    Scaffold(
        topBar = {
            Title(title = "My \nPortfolio")
        },
        bottomBar = {
            BottomNavigationBar(
                modifier = Modifier,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                items = listOf(
                    BottomNavigationItem(
                        iconRes = R.drawable.avd_outline_add_notes_24,
                        label = stringResource(id = R.string.portfolio_label),
                        contentDescription = stringResource(id = R.string.portfolio_label)
                    ),
                    BottomNavigationItem(
                        iconRes = R.drawable.avd_outline_donut_large_24,
                        label = stringResource(id = R.string.summary_label),
                        contentDescription = stringResource(id = R.string.summary_label),
                    ),
                    BottomNavigationItem(
                        iconRes = R.drawable.avd_outline_analytics_24,
                        label = stringResource(id = R.string.simulation_label),
                        contentDescription = stringResource(id = R.string.simulation_label),
                    ),
                )
            )
        },
        floatingActionButton = {

            CustomFloatingActionButton(
                onClick = { /* Handle click action */ },
                modifier = Modifier
                    .padding(bottom = 12.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = painterResource(id = R.drawable.outline_add_24),
                contentDescription = stringResource(id = R.string.add_FAB_description)
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PortfolioScreenPreview() {
    IgnitionFinanceTheme {
        val navController = rememberNavController()
        PortfolioScreen(navController = navController)
    }
}
