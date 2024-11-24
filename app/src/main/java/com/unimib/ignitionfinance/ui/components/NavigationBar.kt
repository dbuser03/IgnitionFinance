package com.unimib.ignitionfinance.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.tooling.preview.Preview
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme

@Composable
fun CustomNavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    tonalElevation: Dp = NavigationBarDefaults.Elevation,
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
    content: @Composable RowScope.() -> Unit
) {
    NavigationBar(
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        windowInsets = windowInsets
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationBarPreview() {
    IgnitionFinanceTheme {
        CustomNavigationBar {
            NavigationBarItem(
                selected = true,
                onClick = { /* Handle Home Click */ },
                label = { Text("Home") },
                icon = { Icon(Icons.Filled.Home, contentDescription = null) }
            )
            NavigationBarItem(
                selected = false,
                onClick = { /* Handle Profile Click */ },
                label = { Text("Profile") },
                icon = { Icon(Icons.Filled.AccountCircle, contentDescription = null) }
            )
        }
    }
}