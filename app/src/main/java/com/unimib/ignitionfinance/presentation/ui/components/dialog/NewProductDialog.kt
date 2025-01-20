package com.unimib.ignitionfinance.presentation.ui.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.presentation.ui.theme.IgnitionFinanceTheme
import com.unimib.ignitionfinance.presentation.ui.theme.TypographyMedium
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun NewProductDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String?, String?, String?, String?) -> Unit,
    dialogTitle: String,
) {
    var isinInput by remember { mutableStateOf<String?>(null) }
    var tickerInput by remember { mutableStateOf<String?>(null) }
    var dateInput by remember { mutableStateOf<String?>(null) }
    var amountInput by remember { mutableStateOf<String?>(null) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isInputValid = errorMessage == null

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface,
        confirmButton = {
            TextButton(
                onClick = {
                    if (isInputValid) {
                        onConfirmation(isinInput, tickerInput, dateInput, amountInput)
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
                NewProductTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Product ISIN",
                    onValueChange = { input ->
                        isinInput = input
                        if (input != null) {
                            errorMessage = if (input.isEmpty()) {
                                "ISIN is required"
                            } else null
                        }
                    },
                    errorMessage = errorMessage
                )

                Spacer(modifier = Modifier.height(5.dp))

                NewProductTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Product TICKER",
                    onValueChange = { input ->
                        tickerInput = input
                        errorMessage = if (input?.length != 4) {
                            "Ticker must be exactly 4 characters"
                        } else null
                    },
                    errorMessage = errorMessage
                )

                Spacer(modifier = Modifier.height(5.dp))

                NewProductTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Purchase Date",
                    onValueChange = { input ->
                        dateInput = input
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val parsedDate = try {
                            if (input != null) {
                                dateFormat.parse(input)
                            } else {
                                null
                            }
                        } catch (e: Exception) {
                            null
                        }
                        errorMessage = if (parsedDate == null) {
                            "Invalid date format"
                        } else null
                    },
                    errorMessage = errorMessage,
                    //keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(5.dp))

                NewProductTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "€ Amount",
                    onValueChange = { input ->
                        amountInput = input
                        if (input != null) {
                            errorMessage = if (input.isEmpty() || input.toDoubleOrNull() == null) {
                                "Amount must be a valid number"
                            } else null
                        }
                    },
                    errorMessage = errorMessage,
                    //keyboardType = KeyboardType.Number
                )
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewNewProductDialog() {
    IgnitionFinanceTheme {
        var showDialog by remember { mutableStateOf(true) }

        if (showDialog) {
            NewProductDialog(
                onDismissRequest = { showDialog = false },
                onConfirmation = { isin, ticker, date, amount ->
                    println("Confirmed: ISIN=$isin, Ticker=$ticker, Date=$date, Amount=$amount")
                    showDialog = false
                },
                dialogTitle = "Add a new product"
            )
        }
    }
}