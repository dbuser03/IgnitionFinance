package com.unimib.ignitionfinance.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.unimib.ignitionfinance.ui.screens.IntroScreen
import com.unimib.ignitionfinance.ui.screens.PortfolioScreen
import com.unimib.ignitionfinance.ui.screens.SimulationScreen
import com.unimib.ignitionfinance.ui.screens.SummaryScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Destinations.IntroScreen.route,
        enterTransition = {
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
        }
    ) {
        composable(route = Destinations.IntroScreen.route) {
            IntroScreen(navController)
        }
        composable(route = Destinations.PortfolioScreen.route) {
            PortfolioScreen(navController)
        }
        composable(route = Destinations.SimulationScreen.route) {
            SimulationScreen(navController)
        }
        composable(route = Destinations.SummaryScreen.route) {
            SummaryScreen(navController)
        }
    }
}