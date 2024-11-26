package com.unimib.ignitionfinance.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.ui.components.IntroImage
import com.unimib.ignitionfinance.ui.components.Title
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme
import kotlinx.coroutines.delay

@Composable
fun IntroScreen() {
    val initialOffset = 1000f
    val targetOffset = 24f
    val animatedOffset = remember { Animatable(initialOffset) }

    LaunchedEffect(Unit) {
        delay(350)
        animatedOffset.animateTo(
            targetValue = targetOffset,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Scaffold(
        topBar = {
            Title(title = stringResource(id = R.string.app_title))
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = animatedOffset.value.dp)
                ) {
                    IntroImage()
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun IntroScreenPreview() {
    IgnitionFinanceTheme {
        IntroScreen()
    }
}