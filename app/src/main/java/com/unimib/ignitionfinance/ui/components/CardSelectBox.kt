package com.unimib.ignitionfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme

@Composable
fun CardSelectBox(
    text: String,
    displayedTexts: List<String>,
    iconResId: Int,
    initialSelectedText: String? = null // New parameter for pre-selection
) {
    var isSelected by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(initialSelectedText) } // Initialize with the given value

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(152.dp)
            .background(MaterialTheme.colorScheme.background)
            .clickable {
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
                .padding(top = 32.dp),
            verticalArrangement = Arrangement.Top
        ) {
            displayedTexts.forEach { displayedText ->
                Text(
                    text = displayedText,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        textDecoration = if (selectedText == displayedText) TextDecoration.Underline else TextDecoration.None
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        selectedText = displayedText
                    }
                )
            }
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                IconWithBackground(
                    icon = painterResource(id = iconResId)
                )
            }
        }
    }
}

@Preview
@Composable
fun CardSelectBoxPreview() {
    IgnitionFinanceTheme {
        CardSelectBox(
            text = "Monthly withdrawals (no pension)",
            displayedTexts = listOf("REAL", "NOMINAL", "INDEXED"),
            iconResId = R.drawable.outline_person_apron_24,
            initialSelectedText = "NOMINAL"
        )
    }
}