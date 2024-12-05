package com.unimib.ignitionfinance.presentation.ui.components.registration

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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.domain.validation.RegistrationValidationResult
import com.unimib.ignitionfinance.domain.validation.RegistrationValidator
import com.unimib.ignitionfinance.presentation.utils.getTextFieldColors

@Composable
fun SurnameTextField(
    surname: String,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = modifier) {
        OutlinedTextField(
            value = surname,
            onValueChange = {
                onNameChange(it)
                errorMessage = when (val result = RegistrationValidator.validateSurname(it)) {
                    is RegistrationValidationResult.Failure -> result.message
                    RegistrationValidationResult.Success -> null
                }
            },
            isError = errorMessage != null,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            label = { Text("Surname") },
            shape = RoundedCornerShape(56.dp),
            colors = getTextFieldColors(isError = errorMessage != null),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (errorMessage == null) {
                        keyboardController?.hide()
                    }
                }
            )
        )
    }
}


