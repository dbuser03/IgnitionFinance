package com.unimib.ignitionfinance.presentation.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.theme.PrimaryBlack
import com.unimib.ignitionfinance.presentation.ui.theme.PrimaryWhite
import kotlinx.coroutines.delay

@Composable
fun IntroImage(onNavigate: () -> Unit) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val videoHeight = screenHeight * 0.8f

    val introText = stringResource(id = R.string.intro_description)

    var textVisible by remember { mutableStateOf(false) }
    var isFabClickable by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1000)
        textVisible = true
    }

    LaunchedEffect(textVisible) {
        if (textVisible) {
            delay(500)
            isFabClickable = true
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
        BackgroundImage()

        AnimatedText(text = introText, visible = textVisible)

        CustomFAB(
            onClick = {
                if (isFabClickable) {
                    onNavigate()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 56.dp),
            containerColor = PrimaryWhite,
            contentColor = PrimaryBlack,
            icon = painterResource(id = R.drawable.outline_keyboard_arrow_up_24),
            contentDescription = stringResource(id = R.string.swipe_up_FAB_description)
        )
    }
}

@Composable
fun BackgroundImage() {
    Image(
        painter = painterResource(id = R.drawable.intro_screen_image),
        contentDescription = stringResource(id = R.string.intro_bg_description),
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun AnimatedText(text: String, visible: Boolean) {
    val initialTextColor = MaterialTheme.colorScheme.secondary
    val changedTextColor = MaterialTheme.colorScheme.primary

    val animatedText = remember { mutableIntStateOf(0) }

    if (visible) {
        LaunchedEffect(Unit) {
            for (i in text.indices) {
                delay(18)
                animatedText.intValue = i + 1
            }
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(1000)),
        exit = fadeOut(tween(1000))
    ) {
        Text(
            text = buildAnnotatedString {
                for (i in text.indices) {
                    withStyle(
                        style = SpanStyle(
                            color = if (i < animatedText.intValue) changedTextColor else initialTextColor
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
}