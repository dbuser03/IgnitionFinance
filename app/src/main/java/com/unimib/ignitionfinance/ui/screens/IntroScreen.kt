package com.unimib.ignitionfinance.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.ui.components.VideoPlaceholder
import com.unimib.ignitionfinance.ui.components.Title
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme
import kotlinx.coroutines.delay

@Composable
fun IntroScreen() {
    val initialOffset = 1000f
    val targetOffset = 0f
    val animatedOffset = remember { Animatable(initialOffset) }

    LaunchedEffect(Unit) {
        delay(350)
        animatedOffset.animateTo(
            targetValue = targetOffset,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Title(title = stringResource(id = R.string.app_title))
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = animatedOffset.value.dp)
        ) {
            VideoPlaceholder()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IntroScreenPreview() {
    IgnitionFinanceTheme {
        IntroScreen()
    }
}
