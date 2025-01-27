package com.unimib.ignitionfinance.presentation.ui.components.summary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SummaryChart() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Qui potrai inserire il tuo PieChart
            // Il Box fornisce uno spazio centrato per il grafico
            // con padding interno e altezza massima di 300.dp
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    heightDp = 300,
    name = "Pie Chart Card Preview"
)
@Composable
fun PieChartCardPreview() {
    MaterialTheme {
        SummaryChart()
    }
}