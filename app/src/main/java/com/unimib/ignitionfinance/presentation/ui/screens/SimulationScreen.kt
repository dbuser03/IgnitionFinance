package com.unimib.ignitionfinance.presentation.ui.screens

import BottomNavigationBarInstance
import android.app.Activity
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.unimib.ignitionfinance.BuildConfig
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.components.CustomFAB
import com.unimib.ignitionfinance.presentation.ui.components.simulation.SimulationBarsForFour
import com.unimib.ignitionfinance.presentation.ui.components.title.TitleWithButton
import com.unimib.ignitionfinance.presentation.viewmodel.PortfolioScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.SettingsScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.SimulationScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.SummaryScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SimulationScreen(
    navController: NavController,
    viewModel: SimulationScreenViewModel = hiltViewModel(),
    settingsViewModel: SettingsScreenViewModel = hiltViewModel(),
    summaryViewModel: SummaryScreenViewModel = hiltViewModel(),
    portfolioViewModel: PortfolioScreenViewModel =  hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val summaryState by summaryViewModel.state.collectAsState()
    val portfolioState by portfolioViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        settingsViewModel.getUserSettings()
        summaryViewModel.getInvested()
        portfolioViewModel.getCash()
    }

    BackHandler(enabled = true) {
        (context as? Activity)?.moveTaskToBack(true)
    }

    Scaffold(
        topBar = {
            TitleWithButton(
                title = stringResource(id = R.string.simulation_title),
                description = stringResource(id = R.string.simulation_description),
                navController = navController
            )
        },
        bottomBar = {
            BottomNavigationBarInstance(navController = navController)
        },
        floatingActionButton = {
            CustomFAB(
                onClick = {
                    viewModel.startSimulation(apiKey = BuildConfig.ALPHAVANTAGE_API_KEY)
                },
                modifier = Modifier.padding(bottom = 12.dp),
                icon = painterResource(id = R.drawable.outline_autoplay_24),
                contentDescription = stringResource(id = R.string.simulate_FAB_description)
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (val simulationState = state.simulationState) {
                    is UiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                    }

                    is UiState.Success -> {
                        val (results, _) = simulationState.data
                        val networth = portfolioState.cash.toDouble() + summaryState.invested
                        SimulationBarsForFour(
                            capital1 = formatCapital(networth),
                            capital2 = formatCapital(networth + 50_000),
                            capital3 = formatCapital(networth + 100_000),
                            capital4 = formatCapital(networth + 150_000),
                            percentage1 = results[0].successRate,
                            percentage2 = results[1].successRate,
                            percentage3 = results[2].successRate,
                            percentage4 = results[3].successRate
                        )
                    }

                    is UiState.Error -> {
                        Text(
                            text = "Error: ${simulationState.message}",
                            modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                        )
                    }

                    else -> {
                        Text(text = "")
                    }
                }
            }
        }
    )
}


fun formatCapital(value: Double): String {
    return when {
        value < 1_000 -> "$value"
        value < 1_000_000 -> {
            val thousands = value / 1000.0
            if (thousands % 1.0 == 0.0) "${thousands.toInt()}k"
            else String.format(Locale.US, "%.1fk", thousands)
        }

        value < 100_000_000 -> {
            val millions = value / 1_000_000.0
            String.format(Locale.US, "%.1fM", millions)
        }

        value < 1_000_000_000 -> {
            val millions = value / 1_000_000
            "${millions}M"
        }

        else -> {
            val billions = value / 1_000_000_000.0
            if (billions < 100) String.format(Locale.US, "%.1fMLD", billions)
            else "${billions.toInt()}MLD"
        }
    }
}
