package com.unimib.ignitionfinance.presentation.ui.components.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.domain.validation.LoginValidationResult
import com.unimib.ignitionfinance.domain.validation.LoginValidator
import com.unimib.ignitionfinance.presentation.navigation.Destinations
import com.unimib.ignitionfinance.presentation.ui.components.CustomFAB
import com.unimib.ignitionfinance.presentation.ui.theme.TypographyMedium

@Composable
fun LoginForm(
    navController: NavController,
    onLoginClick: (String, String) -> Unit
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    val isFormValid = remember(email.value, password.value) {
        val result = LoginValidator.validateLoginForm(email.value, password.value)
        result is LoginValidationResult.Success
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        LoginEmailTextField(
            email = email.value,
            onEmailChange = { email.value = it },
            passwordFocusRequester = passwordFocusRequester,
            modifier = Modifier
                .focusRequester(emailFocusRequester)
        )

        Spacer(modifier = Modifier.padding(top = 4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LoginPasswordTextField(
                password = password.value,
                onPasswordChange = { password.value = it },
                modifier = Modifier
                    .focusRequester(passwordFocusRequester)
                    .weight(1.0f)
            )
            CustomFAB(
                modifier = Modifier
                    .padding(top = 8.dp),
                icon = painterResource(id = R.drawable.outline_keyboard_arrow_right_24),
                contentDescription = stringResource(id = R.string.registration_FAB_description),
                onClick = {
                    if (isFormValid) {
                        onLoginClick(email.value, password.value)
                    }
                },
                containerColor = if (isFormValid) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondary
                },
                contentColor = if (isFormValid) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSecondary
                }
            )
        }

        TextButton(
            onClick = {
                //navController.navigate(Destinations.ForgotPasswordScreen.route)
            },
            modifier = Modifier
                .align(Alignment.Start)
        ) {
            Text(
                "Forgot Password?",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }


        TextButton(
            onClick = {
                navController.navigate(Destinations.RegistrationScreen.route)
            },
            modifier = Modifier
                .align(Alignment.Start)
        ) {
            Text(
                "Create Account",
                style = TypographyMedium.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

