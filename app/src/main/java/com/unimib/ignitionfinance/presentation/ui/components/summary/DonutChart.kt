package com.unimib.ignitionfinance.presentation.ui.components.summary

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.unimib.ignitionfinance.presentation.ui.theme.IgnitionFinanceTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun DonutChart(
    uno: Int,
    due: Int,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            //.padding(16.dp)
            //.fillMaxWidth()
            //.fillMaxHeight(),
    ) {
        Column {

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .height(280.dp)
                        .fillMaxWidth()
                        //.fillMaxHeight(),
                ) {
                    val strokeWidth = 20f
                    val canvasSize = size.width
                    val radius = (canvasSize - strokeWidth) / 2

                    drawArc(
                        color = Color.Gray,
                        startAngle = (uno.toFloat() / 100f * 360f) - 90f,
                        sweepAngle = (due.toFloat() / 100f * 360f),
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(radius * 2, radius * 2)
                    )

                    drawArc(
                        color = Color.Black,
                        startAngle = -90f,
                        sweepAngle = (uno.toFloat() / 100f * 360f) / 2,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(radius * 2, radius * 2)
                    )

                    drawArc(
                        color = Color.Black,
                        startAngle = -90f + (uno.toFloat() / 100f * 360f) / 2,
                        sweepAngle = (uno.toFloat() / 100f * 360f) / 2,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(radius * 2, radius * 2)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PieChartPreview() {
    IgnitionFinanceTheme {
        Box(
            modifier = Modifier
                //.fillMaxWidth()
                //.height(300.dp)
                .padding(16.dp)
        ) {
            DonutChart(
                uno = 80,
                due = 20
            )
        }
    }
}



