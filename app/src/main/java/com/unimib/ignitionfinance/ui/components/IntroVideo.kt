package com.unimib.ignitionfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.ui.theme.*

@Composable
fun VideoPlaceholder() {

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val videoHeight = screenHeight * 0.8f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(videoHeight)
            .background(
                MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = stringResource(id = R.string.lorem_ipsum),
            modifier = Modifier.padding(start = 16.dp, end = 112.dp, bottom = 160.dp),
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onPrimary
            )
        )

        RoundedSwipeUpButton(
            icon = Icons.Filled.KeyboardArrowUp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 52.dp)
        )
    }
}

@Preview
@Composable
fun VideoPlaceholderPreview() {
    IgnitionFinanceTheme {
        VideoPlaceholder()
    }
}

