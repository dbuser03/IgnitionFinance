package com.unimib.ignitionfinance.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface, // Colore del testo
    labelColor: Color = MaterialTheme.colorScheme.primary, // Colore della label
    backgroundColor: Color = MaterialTheme.colorScheme.surface, // Colore dello sfondo
    labelTextStyle: TextStyle = MaterialTheme.typography.bodyMedium // Stile tipografico della label
) {
    var text by remember { mutableStateOf("Input") }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = {
            Text(
                text = "New Value",
                color = labelColor, // Colore della label
                style = labelTextStyle // Applica lo stile personalizzato della label
            )
        },
        shape = RoundedCornerShape(56.dp), // Arrotonda i bordi
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = textColor), // Stile e colore del testo
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary, // Bordo attivo
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant, // Bordo inattivo
            cursorColor = MaterialTheme.colorScheme.primary, // Colore del cursore
            focusedLabelColor = labelColor, // Colore della label attiva
            unfocusedLabelColor = labelColor // Colore della label inattiva
        ),
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomTextField() {
    IgnitionFinanceTheme {
        CustomTextField(
            labelTextStyle = MaterialTheme.typography.bodySmall // Passa uno stile personalizzato
        )
    }
}
