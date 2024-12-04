package com.unimib.ignitionfinance.presentation.ui.components.settings.select

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun SelectCard(
    label: String,
    title: String,
    inputText: String,
    displayedTexts: List<String>,
    initialSelectedText: String? = null,
    isExpanded: Boolean,
    onCardClicked: () -> Unit
) {
    var selectedText = remember { mutableStateOf(initialSelectedText) }

    val cardHeight = if (isExpanded) 152.dp + 104.dp + 24.dp else 104.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clickable(onClick = onCardClicked, indication = null, interactionSource = remember { MutableInteractionSource() }),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.secondary),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {

            SelectCardHeader(
                label = label,
                title = title,
                isExpanded = isExpanded,
                onCardClicked = onCardClicked
            )

            if (isExpanded) {
                SelectCardBody(
                    inputText = inputText,
                    displayedTexts = displayedTexts,
                    selectedText = selectedText.value,
                    onTextSelected = { newSelection ->
                        selectedText.value = newSelection
                    }
                )
            }
        }
    }
}
