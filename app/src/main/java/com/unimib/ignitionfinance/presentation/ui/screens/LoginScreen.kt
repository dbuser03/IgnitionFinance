package com.unimib.ignitionfinance.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.components.login.LoginForm
import com.unimib.ignitionfinance.presentation.ui.components.title.TitleWithDescription
import com.unimib.ignitionfinance.presentation.viewmodel.LoginScreenViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    name: String = "",
    surname: String = "",
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
                    onLoginClick = { email, password ->
                        viewModel.login(email, password)
                    },
                    loginState = loginState,
                    navController = navController,
                    viewModel = viewModel,
                    name = name,
                    surname = surname
                )

                Button(
                    onClick = { viewModel.deleteAllUsers() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Text(text = "Delete All Users (DEV)")
                }
            }
        }
    )
}