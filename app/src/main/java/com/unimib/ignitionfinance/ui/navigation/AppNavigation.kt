package com.unimib.ignitionfinance.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavGraph(navController = navController)
}