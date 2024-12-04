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

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface,
        confirmButton = {
            TextButton(
                onClick = {
                    val validationResult = InputValidator.validate(textInput, prefix)
                    if (validationResult is ValidationResult.Success) {
                        onConfirmation(textInput)
                    } else {
                        errorMessage = (validationResult as ValidationResult.Failure).message
                    }
                },
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    "Confirm",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = TypographyMedium.bodyMedium.fontWeight,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                },
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
                    onConfirm = { input ->
                        textInput = input
                        val validationResult = InputValidator.validate(input, prefix)
                        if (validationResult is ValidationResult.Success) {
                            onConfirmation(input)
                        } else {
                            errorMessage = (validationResult as ValidationResult.Failure).message
                        }
                    },
                    errorMessage = errorMessage
                )
            }
        },
    )
}


