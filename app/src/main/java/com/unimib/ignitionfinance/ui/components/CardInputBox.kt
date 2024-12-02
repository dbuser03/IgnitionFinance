package com.unimib.ignitionfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import com.unimib.ignitionfinance.R

@Composable
fun CardInputBox(
    text: String,
    prefix: String,
    inputValue: MutableState<TextFieldValue>,
    iconResId: Int,
) {
    var showDialog by remember { mutableStateOf(false) }
    var updatedValue by remember { mutableStateOf(inputValue.value.text) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .align(Alignment.BottomStart)
        ) {
            TextButton(
                onClick = {
                    showDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(0.dp),
                shape = RectangleShape
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "$prefix ",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = inputValue.value.text,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
            ) {
                IconWithBackground(
                    icon = painterResource(id = iconResId)
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = { Text("Enter value") },
            text = {
                Text("Enter the new value for $prefix")
            },
            confirmButton = {
                Button(
                    onClick = {
                        inputValue.value = TextFieldValue(updatedValue)
                        showDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview
@Composable
fun WithdrawalsNoPensionInputPreview() {
    IgnitionFinanceTheme {
        val inputValue = remember { mutableStateOf(TextFieldValue("----")) }
        CardInputBox(
            text = "Monthly withdrawals (no pension)",
            prefix = "€",
            inputValue = inputValue,
            iconResId = R.drawable.outline_person_apron_24
        )
    }
}

@Preview
@Composable
fun WithdrawalsPensionInputPreview() {
    IgnitionFinanceTheme {
        val inputValue = remember { mutableStateOf(TextFieldValue("----")) }
        CardInputBox(
            text = "Monthly withdrawals (pension)",
            prefix = "€",
            inputValue = inputValue,
            iconResId = R.drawable.outline_person_4_24
        )
    }
}