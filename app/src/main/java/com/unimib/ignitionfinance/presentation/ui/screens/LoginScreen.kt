package com.unimib.ignitionfinance.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.navigation.Destinations
import com.unimib.ignitionfinance.presentation.ui.components.login.LoginForm
import com.unimib.ignitionfinance.presentation.ui.components.title.TitleWithDescription
import com.unimib.ignitionfinance.presentation.viewmodel.LoginScreenViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginScreenViewModel = hiltViewModel()
) {
    val loginState by viewModel.loginState.collectAsState()

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
                LoginForm(
                    navController,
                    onLoginClick = { email, password ->
                        viewModel.login(email, password)
                    },
                    loginState = loginState
                )
            }
        }
    )

    when (loginState) {
        is LoginScreenViewModel.LoginState.Success -> {
            navController.navigate(Destinations.PortfolioScreen.route) {
                popUpTo(Destinations.LoginScreen.route) { inclusive = true }
            }
        }
        else -> Unit
    }
}