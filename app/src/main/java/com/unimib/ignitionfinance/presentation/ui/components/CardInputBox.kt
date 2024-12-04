package com.unimib.ignitionfinance.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.domain.model.InputBoxData

@Composable
fun CardInputBox(inputBoxData: InputBoxData, isEnabled: Boolean) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = inputBoxData.label,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .align(Alignment.BottomStart)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = {
                            if (isEnabled) {
                                showDialog = true
                            }
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    .padding(0.dp)
            ) {
                DisplayInputValue(
                    prefix = inputBoxData.prefix,
                    inputValue = inputBoxData.inputValue.value.text
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
            ) {
                IconWithBackground(
                    icon = painterResource(id = inputBoxData.iconResId),
                )
            }
        }
    }

    if (showDialog) {
        CustomDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = { input ->
                showDialog = false
                // Update the information on the database
            },
            dialogTitle = "Update the amount",
        )
    }
}

