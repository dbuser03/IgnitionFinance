package com.unimib.ignitionfinance.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    labelColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    labelTextStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = { input ->
            if (input.all { it.isDigit() }) {
                text = input
            }
        },
        label = if (text.isEmpty()) {
            {
                Text(
                    text = "Input",
                    color = labelColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else null,
        shape = RoundedCornerShape(56.dp),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = textColor),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = labelColor,
            unfocusedLabelColor = labelColor
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomTextField() {
    IgnitionFinanceTheme {
        CustomTextField(
            labelTextStyle = MaterialTheme.typography.bodySmall
        )
    }
}
