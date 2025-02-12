package com.unimib.ignitionfinance.presentation.ui.components.settings.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun InputBoxBody(
    prefix: String,
    inputValue: String,
    padding: Dp = 0.dp,
    textStyle: TextStyle = MaterialTheme.typography.headlineMedium,
    animated: Boolean = false
) {
    var shouldAnimate by remember { mutableStateOf(animated) }

    LaunchedEffect(inputValue) {
        if (inputValue != "Loading ...") {
            delay(1000)
            shouldAnimate = false
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = padding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$prefix ",
            style = textStyle,
            color = MaterialTheme.colorScheme.secondary
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (shouldAnimate) {
                AnimatedInputValue(
                    inputValue = inputValue,
                    textStyle = textStyle
                )
            } else {
                Text(
                    text = inputValue,
                    style = textStyle,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/*@Composable
private fun AnimatedInputValue(
    inputValue: String,
    textStyle: TextStyle
) {
    Row(
        modifier = Modifier.graphicsLayer(clip = false)
    ) {
        inputValue.forEachIndexed { index, char ->
            var visible by remember { mutableStateOf(false) }

            LaunchedEffect(char, index) {
                delay(index * 30L)
                visible = true
            }

            AnimatedVisibility(
                visible = visible,
                enter = slideInHorizontally(
                    initialOffsetX = { 10 },
                    animationSpec = tween(durationMillis = 100)
                ) + fadeIn(animationSpec = tween(durationMillis = 100)),
                exit = ExitTransition.None
            ) {
                Text(
                    text = char.toString(),
                    style = textStyle,
                    color = MaterialTheme.colorScheme.primary,
                    softWrap = false
                )
            }
        }
    }
}*/