package com.unimib.ignitionfinance.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableSelectCard(
    label: String,
    title: String,
    modifier: Modifier = Modifier,
    inputText: String,
    displayedTexts: List<String>,
    initialSelectedText: String? = null,
    isExpanded: Boolean,
    onCardClicked: () -> Unit
) {
    val cardSelectBoxHeight = 152.dp
    val compactHeight = 104.dp

    val cardHeight by animateDpAsState(
        targetValue = if (isExpanded) {
            cardSelectBoxHeight + compactHeight + 24.dp
        } else {
            compactHeight
        },
        label = ""
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clickable(
                onClick = onCardClicked,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
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
                .fillMaxSize()
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (isExpanded) {
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        CardSelectBox(
                            text = inputText,
                            displayedTexts = displayedTexts,
                            initialSelectedText = initialSelectedText
                        )
                    }
                }
            }
        }
    }
}