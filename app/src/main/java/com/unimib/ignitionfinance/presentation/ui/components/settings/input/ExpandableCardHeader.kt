package com.unimib.ignitionfinance.presentation.ui.components.settings.input

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.unimib.ignitionfinance.presentation.ui.components.settings.ExpandableIcon
import com.unimib.ignitionfinance.presentation.ui.components.settings.SettingsCardTitle

@Composable
fun ExpandableCardHeader(
    label: String,
    title: String,
    isExpanded: Boolean,
    onCardClicked: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsCardTitle(label = label, title = title)
        ExpandableIcon(isExpanded = isExpanded, onClick = onCardClicked)
    }
}
