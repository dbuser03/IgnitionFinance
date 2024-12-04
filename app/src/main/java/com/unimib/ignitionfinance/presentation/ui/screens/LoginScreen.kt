package com.unimib.ignitionfinance.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.components.CustomFloatingActionButton
import com.unimib.ignitionfinance.presentation.ui.components.TitleWithDescription
import com.unimib.ignitionfinance.presentation.navigation.Destinations
import com.unimib.ignitionfinance.presentation.ui.theme.IgnitionFinanceTheme

@Composable
fun LoginScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TitleWithDescription(title = stringResource(id = R.string.app_title), description = stringResource(id = R.string.login_description))
        },
        floatingActionButton = {
            CustomFloatingActionButton(
                onClick = {
                    navController.navigate(Destinations.PortfolioScreen.route){
                        popUpTo(Destinations.LoginScreen.route) {
                            inclusive = true
                        }
                    launchSingleTop = true
                }
                          },
                modifier = Modifier
                    .padding(bottom = 24.dp),
                icon = painterResource(id = R.drawable.outline_keyboard_arrow_right_24),
                contentDescription = stringResource(id = R.string.login_FAB_description)
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    IgnitionFinanceTheme {
        val navController = rememberNavController()
        LoginScreen(
            navController = navController,
        )
    }
}
