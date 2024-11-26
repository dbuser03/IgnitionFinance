package com.unimib.ignitionfinance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.ui.components.BottomNavigationBar
import com.unimib.ignitionfinance.ui.components.BottomNavigationItem
import com.unimib.ignitionfinance.ui.components.TitleWithDescriptionAndButton
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme

@Composable
fun SimulationScreen() {
    Scaffold(
        topBar = {
            TitleWithDescriptionAndButton(
                title = stringResource(id = R.string.simulation_title),
                description = stringResource(id = R.string.simulation_description)
            )
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
fun SimulationScreenPreview() {
    IgnitionFinanceTheme {
        SimulationScreen()
    }
}
