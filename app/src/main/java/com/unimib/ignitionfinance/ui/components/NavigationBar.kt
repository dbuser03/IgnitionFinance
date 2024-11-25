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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme
import com.unimib.ignitionfinance.ui.theme.TypographyMedium
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.ui.theme.TypographyBold

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color,
    contentColor: Color,
    items: List<BottomNavigationItem>
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    // Variabile per tracciare se una determinata icona è stata già animata
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
                // L'animazione viene eseguita solo se l'elemento è selezionato per la prima volta
                if (isSelected && !animatedState[index]) {
                    atEnd = true
                    animatedState[index] = true // Marcatura per evitare una nuova animazione
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
                    // Se l'elemento è già selezionato, non fare nulla
                    if (selectedIndex != index) {
                        selectedIndex = index
                        atEnd = !atEnd // Esegui l'animazione solo se è cambiata la selezione
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
                    label = "Portfolio",
                    contentDescription = "Portfolio"
                ),
                BottomNavigationItem(
                    iconRes = R.drawable.avd_outline_donut_large_24,
                    label = "Summary",
                    contentDescription = "Summary"
                ),
                BottomNavigationItem(
                    iconRes = R.drawable.avd_outline_analytics_24,
                    label = "Simulation",
                    contentDescription = "Simulation"
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