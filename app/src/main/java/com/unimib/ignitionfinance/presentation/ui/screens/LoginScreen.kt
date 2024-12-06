package com.unimib.ignitionfinance.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.components.title.TitleWithDescription
import com.unimib.ignitionfinance.presentation.ui.components.registration.RegistrationForm
import com.unimib.ignitionfinance.presentation.ui.theme.IgnitionFinanceTheme

@Composable
fun LoginScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TitleWithDescription(title = stringResource(id = R.string.app_title), description = stringResource(id = R.string.login_description))
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                RegistrationForm(navController)
            }
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
