package com.unimib.ignitionfinance.presentation.ui.components.registration

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.domain.validation.RegistrationValidationResult
import com.unimib.ignitionfinance.domain.validation.RegistrationValidator
import com.unimib.ignitionfinance.presentation.navigation.Destinations
import com.unimib.ignitionfinance.presentation.ui.components.CustomFAB
import com.unimib.ignitionfinance.presentation.ui.components.CustomTextField
import com.unimib.ignitionfinance.presentation.viewmodel.RegistrationScreenViewModel

@Composable
fun RegistrationForm(
    onRegisterClick: (String, String) -> Unit,
    registrationState: RegistrationScreenViewModel.RegistrationState,
    navController: NavController,
    viewModel: RegistrationScreenViewModel
) {
    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val nameError = remember { mutableStateOf<String?>(null) }
    val surnameError = remember { mutableStateOf<String?>(null) }
    val emailError = remember { mutableStateOf<String?>(null) }
    val passwordError = remember { mutableStateOf<String?>(null) }

    val nameFocusRequester = remember { FocusRequester() }
    val surnameFocusRequester = remember { FocusRequester() }
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    val nameFocused = remember { mutableStateOf(false) }
    val surnameFocused = remember { mutableStateOf(false) }
    val emailFocused = remember { mutableStateOf(false) }
    val passwordFocused = remember { mutableStateOf(false) }

    val isFabFocused = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val isFormValid = remember(name.value, surname.value, email.value, password.value) {
        val result = RegistrationValidator.validateRegistrationForm(name.value, surname.value, email.value, password.value)
        result is RegistrationValidationResult.Success
    }

    LaunchedEffect(Unit) {
        nameFocusRequester.requestFocus()
    }

    LaunchedEffect(registrationState) {
        if (registrationState is RegistrationScreenViewModel.RegistrationState.Success) {
            navController.navigate(Destinations.PortfolioScreen.route) {
                popUpTo(Destinations.LoginScreen.route) { inclusive = true }
            }

            /*registrationState.authData.let { authData ->
                viewModel.storeUserData(
                    name = name.value,
                    surname = surname.value,
                    authData = authData
                )
            }*/
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
            CustomTextField(
                value = name.value,
                onValueChange = {
                    name.value = it
                    nameError.value = (RegistrationValidator.validateName(it) as? RegistrationValidationResult.Failure)?.message
                },
                label = "Name",
                isError = nameError.value != null,
                nextFocusRequester = surnameFocusRequester,
                imeAction = ImeAction.Next,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(nameFocusRequester)
                    .onFocusChanged {
                        nameFocused.value = it.isFocused
                        if (it.isFocused) isFabFocused.value = false
                    }
            )
            CustomTextField(
                value = surname.value,
                onValueChange = {
                    surname.value = it
                    surnameError.value = (RegistrationValidator.validateSurname(it) as? RegistrationValidationResult.Failure)?.message
                },
                label = "Surname",
                isError = surnameError.value != null,
                nextFocusRequester = emailFocusRequester,
                imeAction = ImeAction.Next,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(surnameFocusRequester)
                    .onFocusChanged {
                        surnameFocused.value = it.isFocused
                        if (it.isFocused) isFabFocused.value = false
                    }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        CustomTextField(
            value = email.value,
            onValueChange = {
                email.value = it
                emailError.value = (RegistrationValidator.validateEmail(it) as? RegistrationValidationResult.Failure)?.message
            },
            label = "Email",
            isError = emailError.value != null,
            nextFocusRequester = passwordFocusRequester,
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Email,
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
                    passwordError.value = (RegistrationValidator.validatePassword(it) as? RegistrationValidationResult.Failure)?.message
                },
                label = "Password",
                isError = passwordError.value != null,
                imeAction = ImeAction.Done,
                isPasswordField = true,
                onImeActionPerformed = {
                    if (isFormValid) {
                        focusManager.clearFocus()
                        onRegisterClick(email.value, password.value)
                        isFabFocused.value = true
                    }
                },
                modifier = Modifier
                    .focusRequester(passwordFocusRequester)
                    .weight(1f)
                    .onFocusChanged {
                        passwordFocused.value = it.isFocused
                        if (it.isFocused) isFabFocused.value = false
                    }
            )


            CustomFAB(
                modifier = Modifier.padding(top = 8.dp),
                icon = painterResource(id = R.drawable.outline_keyboard_arrow_right_24),
                contentDescription = stringResource(id = R.string.registration_FAB_description),
                onClick = {
                    if (isFormValid) {
                        focusManager.clearFocus()
                        onRegisterClick(email.value, password.value)
                        isFabFocused.value = true
                    }
                },
                containerColor = if (isFormValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                contentColor = if (isFormValid) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val selectedError = when {
            isFabFocused.value && registrationState is RegistrationScreenViewModel.RegistrationState.Error -> {
                registrationState.message
            }
            nameFocused.value -> nameError.value
            surnameFocused.value -> surnameError.value
            emailFocused.value -> emailError.value
            passwordFocused.value -> passwordError.value
            else -> null
        }

        selectedError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp, end = 64.dp)
            )
        }
    }
}