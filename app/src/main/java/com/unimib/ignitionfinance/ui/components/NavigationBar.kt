package com.unimib.ignitionfinance.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme
import com.unimib.ignitionfinance.ui.theme.TypographyMedium
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.ui.theme.TypographyBold

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color,
    contentColor: Color,
    items: List<BottomNavigationItem>
) {
    var selectedIndex by remember { mutableIntStateOf(2) }

    NavigationBar(
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.contentDescription,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = if (selectedIndex == index) TypographyBold.bodySmall else TypographyMedium.bodySmall
                    )
                },
                selected = selectedIndex == index,
                onClick = { selectedIndex = index },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    }
}


data class BottomNavigationItem(
    val iconRes: Int,
    val label: String,
    val contentDescription: String? = null
)

@Preview
@Composable
fun BottomNavigationBarPreview() {
    IgnitionFinanceTheme {
        BottomNavigationBar(
            modifier = Modifier,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            items = listOf(
                BottomNavigationItem(
                    iconRes = R.drawable.outline_add_notes_24,
                    label = "Portfolio",
                    contentDescription = "Portfolio"
                ),
                BottomNavigationItem(
                    iconRes = R.drawable.outline_donut_large_24,
                    label = "Summary",
                    contentDescription = "Summary"
                ),
                BottomNavigationItem(
                    iconRes = R.drawable.outline_analytics_24,
                    label = "Simulation",
                    contentDescription = "Simulation"
                ),
            )
        )
    }
}
