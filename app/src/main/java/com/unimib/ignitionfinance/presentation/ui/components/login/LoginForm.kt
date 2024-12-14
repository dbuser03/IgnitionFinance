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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.domain.validation.LoginValidationResult
import com.unimib.ignitionfinance.domain.validation.LoginValidator
import com.unimib.ignitionfinance.presentation.navigation.Destinations
import com.unimib.ignitionfinance.presentation.ui.components.CustomFAB
import com.unimib.ignitionfinance.presentation.ui.components.CustomTextField
import com.unimib.ignitionfinance.presentation.ui.theme.TypographyMedium
import com.unimib.ignitionfinance.presentation.viewmodel.LoginScreenViewModel

@Composable
fun LoginForm(
    onLoginClick: (String, String) -> Unit,
    loginState: LoginScreenViewModel.LoginState,
    navController: NavController,
    viewModel: LoginScreenViewModel,
    name: String,
    surname: String
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val emailError = remember { mutableStateOf<String?>(null) }
    val passwordError = remember { mutableStateOf<String?>(null) }

    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    val emailFocused = remember { mutableStateOf(false) }
    val passwordFocused = remember { mutableStateOf(false) }

    val isFabFocused = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val isFormValid = remember(email.value, password.value) {
        val result = LoginValidator.validateLoginForm(email.value, password.value)
        result is LoginValidationResult.Success
    }

    LaunchedEffect(Unit) {
        emailFocusRequester.requestFocus()
    }

    LaunchedEffect(loginState) {
        if (loginState is LoginScreenViewModel.LoginState.Success) {
            val authData = loginState.authData
            val name = name
            val surname = surname

            viewModel.storeUserData(
                name = name,
                surname = surname,
                authData = authData
            )

            navController.navigate(Destinations.PortfolioScreen.route) {
                popUpTo(Destinations.LoginScreen.route) { inclusive = true }
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        CustomTextField(
            value = email.value,
            onValueChange = {
                email.value = it
                emailError.value = (LoginValidator.validateEmail(it) as? LoginValidationResult.Failure)?.message
            },
            label = "Email",
            isError = emailError.value != null,
            nextFocusRequester = passwordFocusRequester,
            imeAction = ImeAction.Next,
            modifier = Modifier
                .focusRequester(emailFocusRequester)
                .onFocusChanged {
                    emailFocused.value = it.isFocused
                    if (it.isFocused) isFabFocused.value = false
                }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomTextField(
                value = password.value,
                onValueChange = {
                    password.value = it
                    passwordError.value = (LoginValidator.validatePassword(it) as? LoginValidationResult.Failure)?.message
                },
                label = "Password",
                isError = passwordError.value != null,
                imeAction = ImeAction.Done,
                isPasswordField = true,
                onImeActionPerformed = {
                    if (isFormValid) {
                        focusManager.clearFocus()
                        onLoginClick(email.value, password.value)
                        isFabFocused.value = true
                    }
                },
                modifier = Modifier
                    .focusRequester(passwordFocusRequester)
                    .weight(1.0f)
                    .onFocusChanged {
                        passwordFocused.value = it.isFocused
                        if (it.isFocused) isFabFocused.value = false
                    }
            )

            CustomFAB(
                modifier = Modifier
                    .padding(top = 8.dp),
                icon = painterResource(id = R.drawable.outline_keyboard_arrow_right_24),
                contentDescription = stringResource(id = R.string.login_FAB_description),
                onClick = {
                    if (isFormValid) {
                        focusManager.clearFocus()
                        isFabFocused.value = true
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

        Spacer(modifier = Modifier.height(16.dp))

        val selectedError = when {
            isFabFocused.value && loginState is LoginScreenViewModel.LoginState.Error -> {
                loginState.message
            }
            emailFocused.value -> emailError.value
            passwordFocused.value -> passwordError.value
            else -> null
        }

        selectedError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(start = 12.dp, end = 64.dp)
            )
        }

        TextButton(
            onClick = {
                navController.navigate(Destinations.ResetPasswordScreen.route)
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