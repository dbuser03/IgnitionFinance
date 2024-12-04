package com.unimib.ignitionfinance.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.components.navigation.BottomNavigationBar
import com.unimib.ignitionfinance.presentation.ui.components.navigation.BottomNavigationItemModel
import com.unimib.ignitionfinance.presentation.ui.components.Title
import com.unimib.ignitionfinance.presentation.navigation.Destinations
import com.unimib.ignitionfinance.presentation.ui.theme.IgnitionFinanceTheme

@Composable
fun SummaryScreen(navController: NavController) {
    Scaffold(
        topBar = {
            Title(title = stringResource(id = R.string.summary_title))
        },
        bottomBar = {
            BottomNavigationBar(
                modifier = Modifier,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                items = listOf(
                    BottomNavigationItemModel(
                        iconRes = R.drawable.avd_outline_add_notes_24,
                        label = stringResource(id = R.string.portfolio_label),
                        destination = Destinations.PortfolioScreen.route,
                        contentDescription = stringResource(id = R.string.portfolio_label),
                    ),
                    BottomNavigationItemModel(
                        iconRes = R.drawable.avd_outline_donut_large_24,
                        label = stringResource(id = R.string.summary_label),
                        destination = Destinations.SummaryScreen.route,
                        contentDescription = stringResource(id = R.string.summary_label),
                    ),
                    BottomNavigationItemModel(
                        iconRes = R.drawable.avd_outline_analytics_24,
                        label = stringResource(id = R.string.simulation_label),
                        destination = Destinations.SimulationScreen.route,
                        contentDescription = stringResource(id = R.string.simulation_label),
                    ),
                ),
                navController = navController
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SummaryScreenPreview() {
    IgnitionFinanceTheme {
        val navController = rememberNavController()
        SummaryScreen(navController = navController)
    }
}