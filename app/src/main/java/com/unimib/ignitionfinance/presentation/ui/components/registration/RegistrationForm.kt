package com.unimib.ignitionfinance.presentation.ui.components.registration

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.domain.validation.RegistrationValidationResult
import com.unimib.ignitionfinance.domain.validation.RegistrationValidator
import com.unimib.ignitionfinance.presentation.ui.components.CustomFAB

@Composable
fun RegistrationForm(
    onRegisterClick: (String, String) -> Unit
) {
    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val errorMessage = remember { mutableStateOf<String?>(null) }
    val focusedField = remember { mutableStateOf<String>("name") }

    val nameFocusRequester = remember { FocusRequester() }
    val surnameFocusRequester = remember { FocusRequester() }
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    fun validateAndSetError(field: String, value: String): Boolean {
        val result = when (field) {
            "name" -> RegistrationValidator.validateName(value)
            "surname" -> RegistrationValidator.validateSurname(value)
            "email" -> RegistrationValidator.validateEmail(value)
            "password" -> RegistrationValidator.validatePassword(value)
            else -> RegistrationValidationResult.Success
        }

        return when (result) {
            is RegistrationValidationResult.Failure -> {
                errorMessage.value = result.message
                focusedField.value = field
                false
            }
            RegistrationValidationResult.Success -> {
                errorMessage.value = null
                true
            }
        }
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
                onNameChange = {
                    name.value = it
                    validateAndSetError("name", it)
                },
                surnameFocusRequester = surnameFocusRequester,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(nameFocusRequester)
                    .enabled(focusedField.value == "name")
            )
            SurnameTextField(
                surname = surname.value,
                onSurnameChange = {
                    surname.value = it
                    if (validateAndSetError("surname", it)) {
                        focusedField.value = "email"
                    }
                },
                emailFocusRequester = emailFocusRequester,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(surnameFocusRequester)
                    .enabled(focusedField.value == "surname")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        EmailTextField(
            email = email.value,
            onEmailChange = {
                email.value = it
                if (validateAndSetError("email", it)) {
                    focusedField.value = "password"
                }
            },
            passwordFocusRequester = passwordFocusRequester,
            modifier = Modifier
                .focusRequester(emailFocusRequester)
                .enabled(focusedField.value == "email")
        )
        Spacer(modifier = Modifier.padding(top = 4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PasswordTextField(
                password = password.value,
                onPasswordChange = { password.value = it },
                validateAndSetError("password", it),
                modifier = Modifier
                    .focusRequester(passwordFocusRequester)
                    .weight(1.0f)
                    .enabled(focusedField.value == "password")
            )

            CustomFAB(
                modifier = Modifier
                    .padding(top = 8.dp),
                icon = painterResource(id = R.drawable.outline_keyboard_arrow_right_24),
                contentDescription = stringResource(id = R.string.registration_FAB_description),
                onClick = {
                    if (validateAndSetError("name", name.value) &&
                        validateAndSetError("surname", surname.value) &&
                        validateAndSetError("email", email.value) &&
                        validateAndSetError("password", password.value)
                    ) {
                        onRegisterClick(email.value, password.value)
                    }
                },
                containerColor = if (errorMessage.value == null) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondary
                },
                contentColor = if (errorMessage.value == null) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSecondary
                }
            )
        }
    }

    if (errorMessage.value != null) {
        Text(
            text = errorMessage.value ?: "",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}
