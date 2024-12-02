package com.unimib.ignitionfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.R

@Composable
fun CardSelectBox(
    text: String,
    displayedTexts: List<String>,
    initialSelectedText: String? = null
) {
    var isSelected by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(initialSelectedText) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(152.dp)
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                isSelected = !isSelected
            }
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.TopStart)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .padding(top = 24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            displayedTexts.forEach { displayedText ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            selectedText = displayedText
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = displayedText,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            textDecoration = if (selectedText == displayedText) TextDecoration.Underline else TextDecoration.None
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )

                    if (selectedText == displayedText) {
                        IconWithBackground(
                            icon = painterResource(id = R.drawable.outline_check_24)
                        )
                    }
                }
            }
        }
    }
}
