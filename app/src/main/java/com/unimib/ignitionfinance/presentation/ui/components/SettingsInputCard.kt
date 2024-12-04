package com.unimib.ignitionfinance.presentation.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.domain.model.InputBoxData

@Composable
fun ExpandableInputCard(
    label: String,
    title: String,
    modifier: Modifier = Modifier,
    inputBoxDataList: List<InputBoxData>,
    isExpanded: Boolean,
    onCardClicked: () -> Unit
) {
    val cardInputBoxHeight = 64.dp
    val spacerHeight = 24.dp

    val cardHeight by animateDpAsState(
        targetValue = if (isExpanded) {
            val totalInputBoxHeight = cardInputBoxHeight * inputBoxDataList.size
            val totalSpacerHeight = spacerHeight * (inputBoxDataList.size)
            totalInputBoxHeight + totalSpacerHeight + 104.dp
        } else {
            104.dp
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
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
                }
                IconButton(
                    onClick = onCardClicked,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = if (isExpanded) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        },
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(24.dp))
                inputBoxDataList.forEachIndexed { index, inputBoxData ->
                    CardInputBox(inputBoxData = inputBoxData, isEnabled = true)
                    if (index < inputBoxDataList.size - 1) {
                        Spacer(modifier = Modifier.height(spacerHeight))
                    }
                }
            }
        }
    }
}
