package com.unimib.ignitionfinance.presentation.ui.components.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

// Crea questo dialog seguendo l'update value dialog come riferimento e guarda il look da figma.
// Usa la preview -> quando il look nella preview è == a quello su figma hai finito
@Composable
fun NewProductDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String?) -> Unit,
    dialogTitle: String
) {
    var textInput by remember { mutableStateOf<String?>(null) }
}