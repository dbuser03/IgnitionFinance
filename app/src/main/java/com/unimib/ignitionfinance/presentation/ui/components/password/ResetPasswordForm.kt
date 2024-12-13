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
import com.unimib.ignitionfinance.domain.validation.ResetValidationResult
import com.unimib.ignitionfinance.domain.validation.ResetValidator
import com.unimib.ignitionfinance.presentation.navigation.Destinations
import com.unimib.ignitionfinance.presentation.ui.components.CustomFAB
import com.unimib.ignitionfinance.presentation.ui.components.CustomTextField
import com.unimib.ignitionfinance.presentation.ui.theme.TypographyMedium
import com.unimib.ignitionfinance.presentation.viewmodel.LoginScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.RegistrationScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.ResetPasswordScreenViewModel

@Composable
fun ResetPasswordForm(
    onResetClick: (String) -> Unit,
    //resetState: ResetPasswordScreenViewModel.resetState,
    navController: NavController,
    viewModel: ResetPasswordScreenViewModel

) {
    val email = remember { mutableStateOf("") }
    val emailError = remember { mutableStateOf<String?>(null) }
    val emailFocusRequester = remember { FocusRequester() }
    val emailFocused = remember { mutableStateOf(false) }
    val isFabFocused = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val isFormValid = remember(email.value) {
        val result = ResetValidator.validateResetForm(email.value)
        result is ResetValidationResult.Success
    }

    LaunchedEffect(Unit) {
        emailFocusRequester.requestFocus()
    }

    LaunchedEffect(resetState) {
        navController.navigate(Destinations.PortfolioScreen.route) {
            popUpTo(Destinations.LoginScreen.route) { inclusive = true }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomTextField(
                value = email.value,
                onValueChange = {
                    email.value = it
                    emailError.value = (ResetValidator.validateEmail(it) as? ResetValidationResult.Failure)?.message
                },
                label = "Email",
                isError = emailError.value != null,
                imeAction = ImeAction.Done,
                onImeActionPerformed = {
                    if (isFormValid) {
                        focusManager.clearFocus()
                        onResetClick(email.value)
                        isFabFocused.value = true
                    }
                },
                modifier = Modifier
                    .focusRequester(emailFocusRequester)
                    .weight(1.0f)
                    .onFocusChanged {
                        emailFocused.value = it.isFocused
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
                        onResetClick(email.value)
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
            isFabFocused.value && resetState is ResetPasswordScreenViewModel.resetState.Error -> {
                resetState.message
            }
            emailFocused.value -> emailError.value
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
    }
}