package com.unimib.ignitionfinance.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme
import com.unimib.ignitionfinance.ui.theme.TypographyMedium

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
                icon = item.icon,
                label = { Text(item.label, style = TypographyMedium.bodySmall) },
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
    val icon: @Composable () -> Unit,
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
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Portfolio") },
                    label = "Portfolio"
                ),
                BottomNavigationItem(
                    icon = { Icon(Icons.Filled.AccountBox, contentDescription = "Summary") },
                    label = "Summary"
                ),
                BottomNavigationItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Simulation") },
                    label = "Simulation"
                ),
            )
        )
    }
}