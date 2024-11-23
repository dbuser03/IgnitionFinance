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
import androidx.compose.ui.res.stringResource
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.ui.theme.*

@Composable
fun VideoPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(680.dp)
            .background(
                MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = stringResource(id = R.string.lorem_ipsum),
            modifier = Modifier.padding(start = 16.dp, end = 112.dp),
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.secondary
            )
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

