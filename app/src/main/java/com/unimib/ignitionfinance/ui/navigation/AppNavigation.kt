package com.unimib.ignitionfinance.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.unimib.ignitionfinance.domain.viewmodel.NavigationViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navigationViewModel: NavigationViewModel = viewModel()
    NavGraph(navController = navController, navigationViewModel = navigationViewModel)
}
