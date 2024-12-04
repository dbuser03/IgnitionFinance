package com.unimib.ignitionfinance.presentation.ui.components.settings.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.presentation.ui.theme.IgnitionFinanceTheme
import com.unimib.ignitionfinance.presentation.ui.theme.TypographyMedium

@Composable
fun CustomDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    dialogTitle: String
) {
    var textInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(textInput) // Passa il valore aggiornato
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
                    labelColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.fillMaxWidth(),
                    onConfirm = { input ->
                        textInput = input // Aggiorna il valore
                        onConfirmation(input) // Esegui la conferma
                    }
                )
            }
        },
    )
}

@Preview
@Composable
fun PreviewCustomDialog() {
    IgnitionFinanceTheme {
        var showDialog by remember { mutableStateOf(true) }
        if (showDialog) {
            CustomDialog(
                onDismissRequest = { showDialog = false },
                onConfirmation = { input ->
                    showDialog = false
                    println("Input text: $input")
                },
                dialogTitle = "Update the amount",
            )
        }
    }
}
