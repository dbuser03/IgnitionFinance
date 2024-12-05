package com.unimib.ignitionfinance.presentation.ui.components.settings.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.presentation.ui.theme.TypographyMedium
import com.unimib.ignitionfinance.domain.validation.InputValidator
import com.unimib.ignitionfinance.domain.validation.ValidationResult

@Composable
fun CustomDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    dialogTitle: String,
    prefix: String
) {
    var textInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val isInputValid = errorMessage == null && textInput.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface,
        confirmButton = {
            TextButton(
                onClick = {
                    if (isInputValid) {
                        onConfirmation(textInput)
                    }
                },
                enabled = isInputValid, // Disabilita il pulsante se l'input non è valido
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    "Confirm",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = TypographyMedium.bodyMedium.fontWeight,
                        color = if (isInputValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() },
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    "Dismiss",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = TypographyMedium.bodyMedium.fontWeight,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        title = {
            Column(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = dialogTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = TypographyMedium.titleLarge.fontWeight
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                CustomTextField(
                    textColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { input ->
                        textInput = input
                        val validationResult = InputValidator.validate(input, prefix)
                        errorMessage = if (validationResult is ValidationResult.Failure) {
                            validationResult.message
                        } else null
                    },
                    errorMessage = errorMessage
                )
            }
        },
    )
}
