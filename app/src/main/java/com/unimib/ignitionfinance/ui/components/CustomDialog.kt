package com.unimib.ignitionfinance.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme
import com.unimib.ignitionfinance.ui.theme.SecondaryGray
import com.unimib.ignitionfinance.ui.theme.TypographyMedium

@Composable
fun CustomDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    dialogTitle: String
) {
    var textInput by remember { mutableStateOf("Input") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp) // Padding orizzontale
            .height(248.dp), // Imposta un'altezza fissa di 248.dp
        title = {
            Column(
                modifier = Modifier.padding(top = 16.dp) // Aggiungi padding sopra al titolo
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
                modifier = Modifier.padding(top = 8.dp) // Aggiungi padding sopra al CustomTextField
            ) {
                CustomTextField(
                    textColor = MaterialTheme.colorScheme.onSurface,
                    labelColor = SecondaryGray, // Pass label color once
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    labelTextStyle = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    textValue = textInput,
                    onTextChange = { textInput = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(textInput)
                }
            ) {
                Text(
                    "Confirm",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = TypographyMedium.bodyMedium.fontWeight // Cambia la tipografia del pulsante
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(
                    "Dismiss",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = TypographyMedium.bodyMedium.fontWeight // Cambia la tipografia del pulsante
                    )
                )
            }
        }
    )
}

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    textValue: String,
    onTextChange: (String) -> Unit,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    labelColor: Color = SecondaryGray, // Default to SecondaryGray
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    labelTextStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {
    OutlinedTextField(
        value = textValue,
        onValueChange = { onTextChange(it) },
        label = {
            Text(
                text = "New Value",
                color = labelColor, // Use the passed labelColor
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
