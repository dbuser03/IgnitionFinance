package com.unimib.ignitionfinance.presentation.ui.components.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DatasetWarningDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String?) -> Unit,
) {
    var textInput by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val isInputValid = errorMessage == null

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    // Confirmation logic here
                    onConfirmation(textInput)
                },
                enabled = isInputValid
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Dismiss")
            }
        },
        title = {
            Text(text = "SP500 Dataset")
        },
        text = {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    "The products used to create the dataset are less than 10 years old. " +
                            "Therefore, if you wish to proceed with the simulation, the dataset " +
                            "will be generated using SP500 (SPY) data available since 1993."
                )
            }
        }
    )
}
