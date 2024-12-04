package com.unimib.ignitionfinance.presentation.ui.components.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@Composable
fun CardItem(
    cardIndex: Int,
    expandedCardIndex: Int,
    listState: LazyListState,
    content: @Composable () -> Unit
) {
    if (expandedCardIndex == cardIndex) {
        LaunchedEffect(cardIndex) {
            listState.animateScrollToItem(cardIndex)
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        content()
    }
}
