package com.unimib.ignitionfinance.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.components.registration.RegistrationForm
import com.unimib.ignitionfinance.presentation.ui.components.title.TitleWithDescription

@Composable
fun RegistrationScreen(navController: NavController) {
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
                RegistrationForm(navController)
            }
        }
    )
}