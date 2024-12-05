package com.unimib.ignitionfinance.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.components.registration.NameTextField
import com.unimib.ignitionfinance.presentation.ui.components.title.TitleWithDescription

@Composable
fun RegistrationScreen(navController: NavController) {
    var name = remember { mutableStateOf("") }

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
                Column(modifier = Modifier.padding(16.dp)) {
                    NameTextField(
                        name = name.value,
                        onNameChange = { name.value = it }
                    )
                }
            }
        }
    )
}