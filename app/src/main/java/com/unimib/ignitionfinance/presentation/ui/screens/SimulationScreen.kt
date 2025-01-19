package com.unimib.ignitionfinance.presentation.ui.screens

import BottomNavigationBarInstance
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.components.CustomFAB
import com.unimib.ignitionfinance.presentation.ui.components.title.TitleWithButton

@Composable
fun SimulationScreen(navController: NavController) {
    val context = LocalContext.current
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
            BottomNavigationBarInstance(
                navController = navController
            )
        },
        floatingActionButton = {
            CustomFAB(
                onClick = { /* Handle click action */ },
                modifier = Modifier
                    .padding(bottom = 12.dp),
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

            }
        }
    )
}