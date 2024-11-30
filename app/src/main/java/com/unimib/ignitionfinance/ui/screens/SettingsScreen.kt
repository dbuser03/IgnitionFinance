package com.unimib.ignitionfinance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unimib.ignitionfinance.ui.components.TitleSettings
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme

@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TitleSettings(
                title = "Settings",
                navController = navController // Passa il NavController
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Aggiungi il contenuto della schermata impostazioni qui
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    IgnitionFinanceTheme {
        val navController = rememberNavController()
        SettingsScreen(navController = navController)
    }
}
