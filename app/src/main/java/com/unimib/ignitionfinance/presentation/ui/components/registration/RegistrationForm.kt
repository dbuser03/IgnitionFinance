package com.unimib.ignitionfinance.presentation.ui.components.registration

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp

@Composable
fun RegistrationForm() {
    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

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

        Spacer(modifier = Modifier.padding(top = 4.dp))
        EmailTextField(
            email = email.value,
            onEmailChange = { email.value = it },
            modifier = Modifier

        )

        Spacer(modifier = Modifier.padding(top = 4.dp))
        PasswordTextField(
            password = password.value,
            onPasswordChange = { password.value = it },
            modifier = Modifier
        )
    }
}