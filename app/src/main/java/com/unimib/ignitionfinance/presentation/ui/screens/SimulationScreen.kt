package com.unimib.ignitionfinance.presentation.ui.screens

import BottomNavigationBarInstance
import android.app.Activity
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.domain.simulation.model.SimulationResult
import com.unimib.ignitionfinance.presentation.model.InputBoxModel
import com.unimib.ignitionfinance.presentation.ui.components.CustomFAB
import com.unimib.ignitionfinance.presentation.ui.components.simulation.SimulationBarsForFour
import com.unimib.ignitionfinance.presentation.ui.components.summary.NetWorthDisplay
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
    portfolioViewModel: PortfolioScreenViewModel = hiltViewModel()
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
                    viewModel.startSimulation()
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
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    is UiState.Success, is UiState.Idle -> {
                        Column {
                            val (results, fuckYouMoney) = when (simulationState) {
                                is UiState.Success -> simulationState.data
                                is UiState.Idle -> {
                                    // Use empty data if no previous simulation exists
                                    state.lastSimulationResult ?: Pair(
                                        List(4) { SimulationResult(0.0) },
                                        0.0
                                    )
                                }
                                else -> Pair(List(4) { SimulationResult(0.0) }, 0.0)
                            }

                            val netWorth = portfolioState.cash.toDouble() + summaryState.invested

                            SimulationBarsForFour(
                                capital1 = formatCapital(netWorth),
                                capital2 = formatCapital(netWorth + 50_000),
                                capital3 = formatCapital(netWorth + 100_000),
                                capital4 = formatCapital(netWorth + 150_000),
                                percentage1 = results[0].successRate,
                                percentage2 = results[1].successRate,
                                percentage3 = results[2].successRate,
                                percentage4 = results[3].successRate
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            NetWorthDisplay(
                                inputBoxModel = InputBoxModel(
                                    label = "Fuck you money (>95%):",
                                    prefix = "€",
                                    key = "Fuck you money",
                                    inputValue = remember { mutableStateOf(TextFieldValue(fuckYouMoney.toString())) },
                                ),
                                netWorth = fuckYouMoney,
                                showVisibilityIcon = false
                            )
                        }
                    }

                    is UiState.Error -> {
                        Text(
                            text = "Error: ${simulationState.message}",
                            modifier = Modifier.align(Alignment.Center)
                        )
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
            if (thousands >= 100) {
                "${thousands.toInt()}k"
            } else {
                if (thousands % 1.0 == 0.0) {
                    "${thousands.toInt()}k"
                } else {
                    String.format(Locale.US, "%.1fk", thousands)
                }
            }
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
