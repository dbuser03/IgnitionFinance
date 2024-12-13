package com.unimib.ignitionfinance.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.components.login.LoginForm
import com.unimib.ignitionfinance.presentation.ui.components.password.ResetPasswordForm
import com.unimib.ignitionfinance.presentation.ui.components.title.TitleWithDescription
import com.unimib.ignitionfinance.presentation.viewmodel.LoginScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.ResetPasswordScreenViewModel

@Composable
fun ResetPasswordScreen(
    navController: NavController,
    viewModel: ResetPasswordScreenViewModel = hiltViewModel()
) {
    val resetState by viewModel.resetState.collectAsState()

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
                ResetPasswordForm(
                    onResetClick = { email ->
                        viewModel.reset(email)
                    },
                    resetState = resetState,
                    navController = navController,
                )
            }
        }
    )
}
