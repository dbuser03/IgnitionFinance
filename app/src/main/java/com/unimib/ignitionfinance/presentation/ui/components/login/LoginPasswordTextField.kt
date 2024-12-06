package com.unimib.ignitionfinance.presentation.ui.components.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.domain.validation.RegistrationValidationResult
import com.unimib.ignitionfinance.domain.validation.RegistrationValidator
import com.unimib.ignitionfinance.presentation.utils.getTextFieldColors
import androidx.compose.ui.res.painterResource
import com.unimib.ignitionfinance.R

@Composable
fun LoginPasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = password,
            onValueChange = {
                onPasswordChange(it)
                errorMessage = when (val result = RegistrationValidator.validatePassword(it)) {
                    is RegistrationValidationResult.Failure -> result.message
                    RegistrationValidationResult.Success -> null
                }
            },
            isError = errorMessage != null,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            label = { Text("Password") },
            shape = RoundedCornerShape(56.dp),
            colors = getTextFieldColors(isError = errorMessage != null),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (errorMessage == null) {
                        keyboardController?.hide()
                    }
                }
            ),
            trailingIcon = {
                val iconRes = if (passwordVisible) R.drawable.outline_visibility_24 else R.drawable.outline_visibility_off_24
                val contentDescription = if (passwordVisible) "Hide password" else "Show password"
                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    modifier = Modifier
                        .padding(end = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = contentDescription,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
            )
        }
    }
}
