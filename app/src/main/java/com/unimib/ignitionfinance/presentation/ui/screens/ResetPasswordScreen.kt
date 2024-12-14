package com.unimib.ignitionfinance.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.components.password.ResetPasswordForm
import com.unimib.ignitionfinance.presentation.ui.components.title.TitleWithDescription
import com.unimib.ignitionfinance.presentation.viewmodel.ResetPasswordScreenViewModel

@Composable
fun ResetPasswordScreen(
    navController: NavController,
    viewModel: ResetPasswordScreenViewModel = hiltViewModel()
) {
    val resetState by viewModel.resetState.collectAsState()

    Scaffold(
        topBar = {
            TitleWithDescription(
                title = stringResource(id = R.string.reset_password_title),
                description = stringResource(id = R.string.reset_password_description)
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
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