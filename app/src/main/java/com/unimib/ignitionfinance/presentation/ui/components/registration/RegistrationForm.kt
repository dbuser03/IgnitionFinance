package com.unimib.ignitionfinance.presentation.ui.components.registration

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegistrationForm() {
    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NameTextField(
                name = name.value,
                onNameChange = { name.value = it },
                modifier = Modifier.weight(1f)
            )
            SurnameTextField(
                surname = surname.value,
                onNameChange = { surname.value = it },
                modifier = Modifier.weight(1f)
            )
        }
    }
}