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
import com.unimib.ignitionfinance.presentation.ui.components.title.TitleWithButton
import com.unimib.ignitionfinance.presentation.viewmodel.SettingsScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.SimulationScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.SummaryScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SimulationScreen(
    navController: NavController,
    viewModel: SimulationScreenViewModel = hiltViewModel(),
    settingsViewModel: SettingsScreenViewModel = hiltViewModel(),
    summaryViewModel: SummaryScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val settingsState by settingsViewModel.state.collectAsState()
    val summaryState by summaryViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        settingsViewModel.getUserSettings()
        summaryViewModel.getInvested()
    }

    val settings = settingsState.settings
    val netWorth = summaryState.netWorth

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
                    if (settings != null) {
                        viewModel.startSimulation(
                            apiKey = BuildConfig.ALPHAVANTAGE_API_KEY,
                            settings = settings,
                            netWorth = netWorth
                        )
                    }
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
                when (state.simulationState) {
                    is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                    is UiState.Success -> Text(text = "Simulation completed!", modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                    is UiState.Error -> Text(text = "Error: ${(state.simulationState as UiState.Error).message}", modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                    else -> Text(text = "Ready to simulate", modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                }
            }
        }
    )
}
