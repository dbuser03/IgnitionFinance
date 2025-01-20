package com.unimib.ignitionfinance.presentation.ui.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.domain.validation.InputValidationResult
import com.unimib.ignitionfinance.domain.validation.SettingsValidator
import com.unimib.ignitionfinance.presentation.ui.theme.TypographyMedium

// Crea questo dialog seguendo l'update value dialog come riferimento e guarda il look da figma.
// Usa la preview -> quando il look nella preview è == a quello su figma hai finito
@Composable
fun NewProductDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String?, String?, String?, String?) -> Unit,
    dialogTitle: String,
    prefix: String
) {
    var ISINInput by remember { mutableStateOf<String?>(null) }
    var ISINErrorMessage by remember { mutableStateOf<String?>(null) }

    var tickerInput by remember { mutableStateOf<String?>(null) }
    var tickerErrorMessage by remember { mutableStateOf<String?>(null) }

    var dateInput by remember { mutableStateOf<String?>(null) }
    var dateErrorMessage by remember { mutableStateOf<String?>(null) }

    var amountInput by remember { mutableStateOf<String?>(null) }
    var amountErrorMessage by remember { mutableStateOf<String?>(null) }

    val isInputValid = ISINErrorMessage == null && tickerErrorMessage == null
            && dateErrorMessage == null && amountErrorMessage == null

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface,
        confirmButton = {
            TextButton(
                onClick = {
                    if (isInputValid) {
                        onConfirmation(ISINInput, tickerInput, dateInput, amountInput)
                    }
                },
                enabled = isInputValid,
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
                UpdateValueTextField(
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { input ->
                        textInput = input
                        val validationResult = SettingsValidator.validate(input, prefix)
                        errorMessage = if (validationResult is InputValidationResult.Failure) {
                            validationResult.message
                        } else null
                    },
                    errorMessage = errorMessage
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
                UpdateValueTextField(
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { input ->
                        textInput = input
                        val validationResult = SettingsValidator.validate(input, prefix)
                        errorMessage = if (validationResult is InputValidationResult.Failure) {
                            validationResult.message
                        } else null
                    },
                    errorMessage = errorMessage
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
                UpdateValueTextField(
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { input ->
                        textInput = input
                        val validationResult = SettingsValidator.validate(input, prefix)
                        errorMessage = if (validationResult is InputValidationResult.Failure) {
                            validationResult.message
                        } else null
                    },
                    errorMessage = errorMessage
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
                UpdateValueTextField(
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { input ->
                        textInput = input
                        val validationResult = SettingsValidator.validate(input, prefix)
                        errorMessage = if (validationResult is InputValidationResult.Failure) {
                            validationResult.message
                        } else null
                    },
                    errorMessage = errorMessage
                )
            }
        },
    )
}