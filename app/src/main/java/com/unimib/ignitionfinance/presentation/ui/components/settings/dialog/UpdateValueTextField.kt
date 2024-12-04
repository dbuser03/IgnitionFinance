package com.unimib.ignitionfinance.presentation.ui.components.settings.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.unimib.ignitionfinance.presentation.ui.theme.IgnitionFinanceTheme

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    labelColor: Color = MaterialTheme.colorScheme.primary,
    onConfirm: (String) -> Unit = {}
) {
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    OutlinedTextField(
        value = text,
        onValueChange = { input ->
            if (input.matches(Regex("^\\d*\\.?\\d*\$"))) {
                text = input
            }
        },
        label = {
            Text(
                text = "New value",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium
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
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = androidx.compose.ui.text.input.ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onConfirm(text)
                keyboardController?.hide()
            }
        ),
        modifier = modifier
            .focusRequester(focusRequester)
            .fillMaxWidth()
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewCustomTextField() {
    IgnitionFinanceTheme {
        CustomTextField()
    }
}