package com.unimib.ignitionfinance.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme

@Composable
fun ExpandableCard(
    label: String,
    title: String,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }

    val cardHeight by animateDpAsState(targetValue = if (isExpanded) 280.dp else 112.dp, label = "")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clickable { isExpanded = !isExpanded },
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 16.dp),
            verticalArrangement = if (isExpanded) Arrangement.Top else Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview
@Composable
fun ExpandableCardExpandedPreview() {
    IgnitionFinanceTheme {
        ExpandableCard(
            label = "NORMAL, RETIREMENT",
            title = "WITHDRAW",
            initiallyExpanded = true
        )
    }
}
