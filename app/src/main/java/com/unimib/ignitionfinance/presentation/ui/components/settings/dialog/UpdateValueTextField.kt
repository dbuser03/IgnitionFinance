package com.unimib.ignitionfinance.presentation.ui.components.settings.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
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
    onConfirm: (String) -> Unit = {},
    errorMessage: String? = null
) {
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val isError = errorMessage != null
    val borderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val labelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

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
                color = labelColor,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        shape = RoundedCornerShape(56.dp),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = textColor),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
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


    errorMessage?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(start = 16.dp)
                .padding(top = 8.dp)
        )
    }
}





@Preview(showBackground = true)
@Composable
fun PreviewCustomTextField() {
    IgnitionFinanceTheme {
        CustomTextField()
    }
}
