package com.unimib.ignitionfinance.ui.components

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme
import com.unimib.ignitionfinance.ui.theme.TypographyMedium
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.ui.theme.TypographyBold
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color,
    contentColor: Color,
    items: List<BottomNavigationItem>
) {
    val hapticFeedback = LocalHapticFeedback.current
    var selectedIndex by remember { mutableIntStateOf(0) }
    val animatedState = remember { mutableStateListOf<Boolean>().apply { repeat(items.size) { add(false) } } }

    NavigationBar(
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedIndex == index
            val animatedImageVector = AnimatedImageVector.animatedVectorResource(id = item.iconRes)
            var atEnd by remember { mutableStateOf(isSelected && !animatedState[index]) }

            LaunchedEffect(isSelected) {
                if (isSelected && !animatedState[index]) {
                    atEnd = true
                    animatedState[index] = true
                }
            }

            NavigationBarItem(
                icon = {
                    val painter = rememberAnimatedVectorPainter(animatedImageVector, atEnd)

                    Icon(
                        painter = painter,
                        contentDescription = item.contentDescription,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = if (isSelected) TypographyBold.bodySmall else TypographyMedium.bodySmall
                    )
                },
                selected = isSelected,
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                    if (selectedIndex != index) {
                        selectedIndex = index
                        atEnd = !atEnd
                    }
                },
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
                    iconRes = R.drawable.avd_outline_add_notes_24,
                    label = stringResource(id = R.string.portfolio_section),
                    contentDescription = stringResource(id = R.string.portfolio_section)
                ),
                BottomNavigationItem(
                    iconRes = R.drawable.avd_outline_donut_large_24,
                    label = stringResource(id = R.string.summary_section),
                    contentDescription = stringResource(id = R.string.summary_section),
                ),
                BottomNavigationItem(
                    iconRes = R.drawable.avd_outline_analytics_24,
                    label = stringResource(id = R.string.simulation_section),
                    contentDescription = stringResource(id = R.string.simulation_section),
                ),
            )
        )
    }
}

data class BottomNavigationItem(
    val iconRes: Int,
    val label: String,
    val contentDescription: String? = null
)