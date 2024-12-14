package com.unimib.ignitionfinance.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.components.RegistrationForm
import com.unimib.ignitionfinance.presentation.ui.components.title.TitleWithDescription
import com.unimib.ignitionfinance.presentation.viewmodel.RegistrationScreenViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RegistrationScreen(
    navController: NavController,
    registrationViewModel: RegistrationScreenViewModel = hiltViewModel(),
) {
    val registrationState by registrationViewModel.registrationState.collectAsState()

    Scaffold(
        topBar = {
            TitleWithDescription(
                title = stringResource(id = R.string.app_title),
                description = stringResource(id = R.string.registration_description)
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                RegistrationForm(
                    onRegisterClick = { email, password ->
                        registrationViewModel.register(email, password)
                    },
                    registrationState = registrationState,
                    navController
                )
            }
        }
    )
}