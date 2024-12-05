package com.unimib.ignitionfinance.presentation.ui.components.registration

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun RegistrationForm() {
    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val birthDate = remember { mutableStateOf("") }

    val nameFocusRequester = remember { FocusRequester() }
    val surnameFocusRequester = remember { FocusRequester() }

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
                surnameFocusRequester = surnameFocusRequester,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(nameFocusRequester)
            )
            SurnameTextField(
                surname = surname.value,
                onSurnameChange = { surname.value = it },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(surnameFocusRequester)
            )
        }
        BirthDateField(
            birthDate = birthDate.value,
            onBirthDateChange = { birthDate.value = it },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )
    }
}