package com.unimib.ignitionfinance.ui.components

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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme

@Composable
fun ExpandableInputCard(
    label: String,
    title: String,
    modifier: Modifier = Modifier,
    inputValues: List<MutableState<TextFieldValue>>,
    prefixes: List<String> = listOf("€"),
    iconResIds: List<Int> = listOf(R.drawable.outline_person_4_24),
    inputBoxes: List<String>,
    isExpanded: Boolean,
    onCardClicked: () -> Unit
) {
    val cardInputBoxHeight = 64.dp
    val spacerHeight = 24.dp

    val cardHeight by animateDpAsState(
        targetValue = if (isExpanded) {
            val totalInputBoxHeight = cardInputBoxHeight * inputBoxes.size
            val totalSpacerHeight = spacerHeight * (inputBoxes.size)
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
                inputBoxes.forEachIndexed { index, boxLabel ->
                    val prefix = prefixes.getOrElse(index) { "€" }
                    val iconResId = iconResIds.getOrElse(index) { R.drawable.outline_person_4_24 }
                    val inputValue = inputValues[index]

                    CardInputBox(
                        text = boxLabel,
                        prefix = prefix,
                        inputValue = inputValue,
                        iconResId = iconResId,
                        isEnabled = true
                    )
                    if (index < inputBoxes.size - 1) {
                        Spacer(modifier = Modifier.height(spacerHeight))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ExpandableInputCardPreview() {
    val isExpanded = false
    val inputValues = List(3) { mutableStateOf(TextFieldValue("100")) }

    IgnitionFinanceTheme {
        ExpandableInputCard(
            label = "NORMAL, RETIREMENT",
            title = "WITHDRAW",
            inputValues = inputValues,
            prefixes = listOf("€", "€", "€"),
            iconResIds = listOf(R.drawable.outline_person_4_24, R.drawable.outline_person_4_24, R.drawable.outline_person_4_24),
            inputBoxes = listOf("Monthly withdrawals (no pension)", "Monthly withdrawals (with pension)", "Monthly withdrawals (extra)"),
            isExpanded = isExpanded,
            onCardClicked = {}
        )
    }
}