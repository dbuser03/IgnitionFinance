package com.unimib.ignitionfinance.presentation.ui.components.summary

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun SummaryChart(
    cash: Double,
    invested: Double
) {
    val investedColor = MaterialTheme.colorScheme.primary
    val cashColor = MaterialTheme.colorScheme.onSecondary

    Card(
        modifier = Modifier
            .wrapContentHeight()
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            Canvas(
                modifier = Modifier.fillMaxWidth()
            ) {
                val investedPercentage = invested
                val cashPercentage = cash

                val strokeWidth = 20f
                val canvasSize = size.width
                val radius = (canvasSize - strokeWidth) / 2

                drawArc(
                    color = investedColor,
                    startAngle = -90f,
                    sweepAngle = (investedPercentage.toFloat() / 100f * 360f),
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                    size = Size(radius * 2, radius * 2)
                )

                drawArc(
                    color = cashColor,
                    startAngle = -90f + (investedPercentage.toFloat() / 100f * 360f),
                    sweepAngle = (cashPercentage.toFloat() / 100f * 360f),
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                    size = Size(radius * 2, radius * 2)
                )
            }
        }
    }
}