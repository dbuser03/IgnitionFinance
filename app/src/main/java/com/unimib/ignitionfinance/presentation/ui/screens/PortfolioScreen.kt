package com.unimib.ignitionfinance.presentation.ui.screens

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
import com.unimib.ignitionfinance.presentation.ui.components.BottomNavigationBar
import com.unimib.ignitionfinance.presentation.ui.components.BottomNavigationItem
import com.unimib.ignitionfinance.presentation.ui.components.CustomFloatingActionButton
import com.unimib.ignitionfinance.presentation.ui.components.Title
import com.unimib.ignitionfinance.presentation.navigation.Destinations
import com.unimib.ignitionfinance.presentation.ui.theme.IgnitionFinanceTheme

@Composable
fun PortfolioScreen(navController: NavController) {
    Scaffold(
        topBar = {
            Title(title = stringResource(id = R.string.portfolio_title))
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
                        destination = Destinations.PortfolioScreen.route,
                        contentDescription = stringResource(id = R.string.portfolio_label)
                    ),
                    BottomNavigationItem(
                        iconRes = R.drawable.avd_outline_donut_large_24,
                        label = stringResource(id = R.string.summary_label),
                        destination = Destinations.SummaryScreen.route,
                        contentDescription = stringResource(id = R.string.summary_label),
                    ),
                    BottomNavigationItem(
                        iconRes = R.drawable.avd_outline_analytics_24,
                        label = stringResource(id = R.string.simulation_label),
                        destination = Destinations.SimulationScreen.route,
                        contentDescription = stringResource(id = R.string.simulation_label),
                    ),
                ),
                navController = navController
            )
        },
        floatingActionButton = {
            CustomFloatingActionButton(
                onClick = { /* Handle click action */ },
                modifier = Modifier
                    .padding(bottom = 12.dp),
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