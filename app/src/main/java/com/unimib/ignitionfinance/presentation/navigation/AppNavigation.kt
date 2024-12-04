package com.unimib.ignitionfinance.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavGraph(navController = navController)
}
