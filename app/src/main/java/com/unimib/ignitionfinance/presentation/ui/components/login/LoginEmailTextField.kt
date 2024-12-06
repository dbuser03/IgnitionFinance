package com.unimib.ignitionfinance.presentation.ui.components.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.domain.validation.LoginValidationResult
import com.unimib.ignitionfinance.domain.validation.LoginValidator
import com.unimib.ignitionfinance.presentation.utils.getTextFieldColors

@Composable
fun LoginEmailTextField(
    email: String,
    onEmailChange: (String) -> Unit,
    passwordFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = email,
            onValueChange = {
                onEmailChange(it)
                errorMessage = when (val result = LoginValidator.validateEmail(it)) {
                    is LoginValidationResult.Failure -> result.message
                    LoginValidationResult.Success -> null
                }
            },
            isError = errorMessage != null,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            label = { Text("Email") },
            shape = RoundedCornerShape(56.dp),
            colors = getTextFieldColors(isError = errorMessage != null),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    if (errorMessage == null) {
                        passwordFocusRequester.requestFocus()
                    }
                }
            )
        )
    }
}