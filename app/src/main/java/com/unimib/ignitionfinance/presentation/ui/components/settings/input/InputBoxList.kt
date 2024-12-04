package com.unimib.ignitionfinance.presentation.ui.components.settings.input

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InputBoxList(
    inputBoxDataList: List<InputBoxData>
) {
    inputBoxDataList.forEachIndexed { index, inputBoxData ->
        SettingsInputBox(inputBoxData = inputBoxData, isEnabled = true)
        if (index < inputBoxDataList.size - 1) {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
