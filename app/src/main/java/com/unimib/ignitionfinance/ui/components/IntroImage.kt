package com.unimib.ignitionfinance.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.ui.theme.*
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle

@Composable
fun IntroImage() {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val videoHeight = screenHeight * 0.8f

    val text = stringResource(id = R.string.lorem_ipsum)
    val initialTextColor = MaterialTheme.colorScheme.secondary
    val changedTextColor = PrimaryWhite

    var textVisible by remember { mutableStateOf(false) }
    var coloredIndices by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1000)
        textVisible = true
    }

    LaunchedEffect(textVisible) {
        if (textVisible) {
            kotlinx.coroutines.delay(500)
            for (i in text.indices) {
                kotlinx.coroutines.delay(18)
                coloredIndices = i + 1
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(videoHeight)
            .background(
                MaterialTheme.colorScheme.onSecondary,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ),
        contentAlignment = Alignment.BottomStart
    ) {
        Image(
            painter = painterResource(id = R.drawable.intro_screen_image),
            contentDescription = "Intro Screen Background",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
            contentScale = ContentScale.Crop
        )

        AnimatedVisibility(
            visible = textVisible,
            enter = fadeIn(tween(1000)),
            exit = fadeOut(tween(1000))
        ) {
            Text(
                text = buildAnnotatedString {
                    for (i in text.indices) {
                        withStyle(
                            style = SpanStyle(
                                color = if (i < coloredIndices) changedTextColor else initialTextColor
                            )
                        ) {
                            append(text[i])
                        }
                    }
                },
                modifier = Modifier.padding(start = 16.dp, end = 112.dp, bottom = 160.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }

        CustomFloatingActionButton(
            onClick = { /* Handle click action */ },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 56.dp),
            containerColor = PrimaryWhite,
            contentColor = PrimaryBlack
        )
    }
}

@Preview
@Composable
fun VideoPlaceholderPreview() {
    IgnitionFinanceTheme {
        IntroImage()
    }
}
