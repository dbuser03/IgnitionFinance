package com.unimib.ignitionfinance.presentation.ui.components.settings.input

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unimib.ignitionfinance.presentation.model.InputBoxModel
import com.unimib.ignitionfinance.presentation.ui.components.CustomIcon
import com.unimib.ignitionfinance.presentation.ui.components.settings.dialog.DialogManager
import com.unimib.ignitionfinance.presentation.viewmodel.SettingsScreenViewModel

@Composable
fun InputBox(
    inputBoxModel: InputBoxModel,
    isEnabled: Boolean,
) {
    var showDialog by remember { mutableStateOf(false) }

    DialogManager(
        showDialog = showDialog,
        onDismissRequest = { showDialog = false },
        onConfirmation = { newValue ->
            showDialog = false

        },
        dialogTitle = "Update the amount",
        prefix = inputBoxModel.prefix
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = inputBoxModel.label,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        onClick = {
                            if (isEnabled) {
                                showDialog = true
                            }
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            ) {
                InputBoxBody(
                    prefix = inputBoxModel.prefix,
                    inputValue = inputBoxModel.inputValue.value.text
                )
            }

            CustomIcon(
                icon = painterResource(id = inputBoxModel.iconResId),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}