package com.unimib.ignitionfinance.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.unimib.ignitionfinance.presentation.ui.screens.IntroScreen
import com.unimib.ignitionfinance.presentation.ui.screens.LoginScreen
import com.unimib.ignitionfinance.presentation.ui.screens.PortfolioScreen
import com.unimib.ignitionfinance.presentation.ui.screens.RegistrationScreen
import com.unimib.ignitionfinance.presentation.ui.screens.SettingsScreen
import com.unimib.ignitionfinance.presentation.ui.screens.SimulationScreen
import com.unimib.ignitionfinance.presentation.ui.screens.SummaryScreen

@Composable
fun NavGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.IntroScreen.route
    ) {
        composable(
            route = Destinations.RegistrationScreen.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            RegistrationScreen(navController)
        }

        composable(
            route = Destinations.LoginScreen.route,
            arguments = listOf(
                navArgument("name") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("surname") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { backStackEntry ->
            LoginScreen(
                navController = navController,
                name = backStackEntry.arguments?.getString("name") ?: "",
                surname = backStackEntry.arguments?.getString("surname") ?: ""
            )
        }
        composable(
            route = Destinations.PortfolioScreen.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            PortfolioScreen(navController)
        }
        composable(
            route = Destinations.SimulationScreen.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            SimulationScreen(navController)
        }
        composable(
            route = Destinations.SummaryScreen.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            SummaryScreen(navController)
        }

        composable(
            route = Destinations.IntroScreen.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ){
            IntroScreen(navController)
        }

        composable(
            route = Destinations.SettingsScreen.route,
            enterTransition = {
                slideInHorizontally(
                    animationSpec = tween(
                        durationMillis = 150,
                        easing = FastOutSlowInEasing
                    )
                ) { it }
            },
            exitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    )
                ) { -it }
            },
            popEnterTransition = {
                slideInHorizontally(
                    animationSpec = tween(
                        durationMillis = 150,
                        easing = FastOutSlowInEasing
                    )
                ) { -it }
            },
            popExitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    )
                ) { it }
            }
        ) {
            SettingsScreen(navController)
        }
    }
}