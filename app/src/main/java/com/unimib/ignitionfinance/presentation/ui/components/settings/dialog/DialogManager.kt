package com.unimib.ignitionfinance.presentation.ui.components.settings.dialog

import androidx.compose.runtime.*

@Composable
fun DialogManager(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    dialogTitle: String,
    prefix: String
) {
    if (showDialog) {
        CustomDialog(
            onDismissRequest = onDismissRequest,
            onConfirmation = onConfirmation,
            dialogTitle = dialogTitle,
            prefix = prefix
        )
    }
}
