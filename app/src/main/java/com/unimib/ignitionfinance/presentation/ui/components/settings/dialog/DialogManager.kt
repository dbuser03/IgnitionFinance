package com.unimib.ignitionfinance.presentation.ui.components.settings.dialog

import androidx.compose.runtime.*

@Composable
fun DialogManager(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    dialogTitle: String,
    prefix: String // Aggiungi il parametro prefix
) {
    if (showDialog) {
        CustomDialog(
            onDismissRequest = onDismissRequest,
            onConfirmation = onConfirmation,
            dialogTitle = dialogTitle,
            prefix = prefix // Passa il prefisso al CustomDialog
        )
    }
}
