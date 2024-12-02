package com.unimib.ignitionfinance.ui.components

import androidx.compose.foundation.layout.fillMaxWidth  // Importa fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme

@Composable
fun CustomDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    dialogTitle: String
) {
    var textInput by remember { mutableStateOf("Input") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth(), // Imposta la larghezza del dialogo a tutta la larghezza
        title = {
            Text(
                text = dialogTitle,
                style = MaterialTheme.typography.titleLarge // Applicare lo stile headlineSmall
            )
        },
        text = {
            // Usa il componente CustomTextField al posto del testo
            CustomTextField(
                textColor = MaterialTheme.colorScheme.onSurface,
                labelColor = MaterialTheme.colorScheme.primary,
                backgroundColor = MaterialTheme.colorScheme.surface,
                labelTextStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),  // Aggiungi anche qui fillMaxWidth per il campo di testo
                textValue = textInput,
                onTextChange = { textInput = it }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(textInput)
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,  // Qui viene usato Modifier
    textValue: String, // Testo attuale del campo
    onTextChange: (String) -> Unit, // Callback per aggiornare il testo
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    labelColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    labelTextStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {
    OutlinedTextField(
        value = textValue,
        onValueChange = { onTextChange(it) },
        label = {
            Text(
                text = "New Value",
                color = labelColor,
                style = labelTextStyle
            )
        },
        shape = RoundedCornerShape(56.dp),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = textColor),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = labelColor,
            unfocusedLabelColor = labelColor
        ),
        modifier = modifier
    )
}

@Preview(showBackground = true)
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
