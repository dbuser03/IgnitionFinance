package com.unimib.ignitionfinance.ui.navigation

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.unimib.ignitionfinance.domain.viewmodel.NavigationViewModel
import com.unimib.ignitionfinance.ui.screens.*

@Composable
fun NavGraph(
    navController: NavHostController,
    navigationViewModel: NavigationViewModel
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Destinations.IntroScreen.route
    ) {
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

        composable(route = Destinations.IntroScreen.route) {
            IntroScreen(
                navController,
                onScreenTouched = {
                    navigationViewModel.markIntroScreenAsVisited()
                    navController.navigate(Destinations.PortfolioScreen.route)
                }
            )
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

    LaunchedEffect(navigationViewModel.hasVisitedIntroScreen.value) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (navigationViewModel.hasVisitedIntroScreen.value && destination.route == Destinations.IntroScreen.route) {
                if (navController.previousBackStackEntry == null) {
                    (context as? Activity)?.finish()
                }
            }
        }
    }
}