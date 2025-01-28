package com.unimib.ignitionfinance.presentation.ui.components.summary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.theme.TypographyMedium
import kotlin.math.roundToInt

@Composable
fun AssetAllocationCard(
    cash: Double,
    invested: Double
) {
    val total = cash + invested

    val cashPercentage = if (total > 0) (cash / total) * 100 else 0.0
    if (total > 0) (invested / total) * 100 else 0.0
    fun roundPercentages(cashPct: Double): Pair<Double, Double> {
        val roundedCash = cashPct.roundToInt()
        val roundedInvested = 100 - roundedCash
        return Pair(roundedCash.toDouble(), roundedInvested.toDouble())
    }

    val (roundedCashPercentage, roundedInvestedPercentage) = roundPercentages(cashPercentage)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
        ) {
            Text(
                text = "Asset allocation:",
                style = TypographyMedium.bodyLarge
            )

            Spacer(
                modifier = Modifier
                    .height(24.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight()
                ) {
                    SummaryChart(
                        invested = roundedInvestedPercentage,
                        cash = roundedCashPercentage
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .wrapContentHeight()
                            .align(Alignment.Center)
                    ) {
                        AssetAllocationLegend(
                            icon = painterResource(id = R.drawable.outline_candlestick_chart_24),
                            title = "Invested",
                            percentage = roundedInvestedPercentage,
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            iconColor = MaterialTheme.colorScheme.onPrimary
                        )
                        AssetAllocationLegend(
                            icon = painterResource(id = R.drawable.outline_monetization_on_24),
                            title = "Cash",
                            percentage = roundedCashPercentage,
                            backgroundColor = MaterialTheme.colorScheme.onSecondary,
                            iconColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}