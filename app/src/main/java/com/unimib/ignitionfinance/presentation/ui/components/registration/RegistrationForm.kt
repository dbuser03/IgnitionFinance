package com.unimib.ignitionfinance.presentation.ui.components.registration

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.domain.validation.RegistrationValidationResult
import com.unimib.ignitionfinance.domain.validation.RegistrationValidator
import com.unimib.ignitionfinance.presentation.navigation.Destinations
import com.unimib.ignitionfinance.presentation.ui.components.CustomFAB

@Composable
fun RegistrationForm(navController: NavController) {
    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val nameFocusRequester = remember { FocusRequester() }
    val surnameFocusRequester = remember { FocusRequester() }
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    val isFormValid = remember(name.value, surname.value, email.value, password.value) {
        val result = RegistrationValidator.validateRegistrationForm(name.value, surname.value, email.value, password.value)
        result is RegistrationValidationResult.Success
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NameTextField(
                name = name.value,
                onNameChange = { name.value = it },
                surnameFocusRequester = surnameFocusRequester,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(nameFocusRequester)
            )
            SurnameTextField(
                surname = surname.value,
                onSurnameChange = { surname.value = it },
                emailFocusRequester = emailFocusRequester,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(surnameFocusRequester)
            )
        }

        Spacer(modifier = Modifier.padding(top = 4.dp))
        EmailTextField(
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
            PasswordTextField(
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
                        navController.navigate(Destinations.PortfolioScreen.route) {
                            popUpTo(Destinations.RegistrationScreen.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                },
                containerColor = if (isFormValid) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                },
                contentColor = if (isFormValid) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                }
            )
        }
    }
}

