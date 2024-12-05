package com.unimib.ignitionfinance.presentation.ui.components.registration

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.unimib.ignitionfinance.domain.validation.RegistrationValidator
import com.unimib.ignitionfinance.domain.validation.ValidationResult

@Composable
fun NameTextField(
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onValueChange: (String?) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val validationResult = RegistrationValidator.validateName(text)
    errorMessage = if (validationResult is ValidationResult.Failure) {
        validationResult.message
    } else {
        null
    }

    val isError = errorMessage != null
    val borderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val labelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    Column {
        OutlinedTextField(
            value = text,
            onValueChange = { input ->
                text = input
                onValueChange(if (input.isBlank()) null else input)
            },
            label = {
                Text(
                    text = "Name",
                    color = MaterialTheme.colorScheme.secondary,
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
            modifier = modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
        )

        if (isError) {
            Text(
                text = errorMessage.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}