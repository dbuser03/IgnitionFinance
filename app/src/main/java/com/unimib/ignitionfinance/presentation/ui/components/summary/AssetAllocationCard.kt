package com.unimib.ignitionfinance.presentation.ui.components.summary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AssetAllocationCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 232.dp)
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Left side content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                Text("Asset allocation:")
                SummaryChart()
            }

            // Vertical divider
            Spacer(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
            )

            // Right side content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                //Text("Contenuto Destro")
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    heightDp = 200,
    name = "Split Card Preview"
)
@Composable
fun AssetAllocationCardPreview() {
    MaterialTheme {
        AssetAllocationCard()
    }
}