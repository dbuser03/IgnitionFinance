package com.unimib.ignitionfinance.ui.screens.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.unimib.ignitionfinance.ui.components.Title
import com.unimib.ignitionfinance.ui.theme.*
import com.unimib.ignitionfinance.R

@Composable
fun IntroScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Title(title = stringResource(id = R.string.app_title))
    }
}

@Preview(showBackground = true)
@Composable
fun IntroScreenPreview() {
    IgnitionFinanceTheme {
        IntroScreen()
    }
}
