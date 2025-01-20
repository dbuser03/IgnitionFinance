package com.unimib.ignitionfinance.presentation.ui.components.dialog

import androidx.compose.runtime.*

@Composable
fun DialogManager(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmation: (String?) -> Unit,
    dialogTitle: String,
    prefix: String
) {
    if (showDialog) {
        UpdateValueDialog(
            onDismissRequest = onDismissRequest,
            onConfirmation = onConfirmation,
            dialogTitle = dialogTitle,
            prefix = prefix
        )
    }
}