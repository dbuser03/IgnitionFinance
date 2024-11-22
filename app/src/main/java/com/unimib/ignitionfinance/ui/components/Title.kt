package com.unimib.ignitionfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.ui.theme.*

@Composable
fun Title(
    title: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(216.dp)
            .background(color = BackgroundWhite),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = title,
            style = TypographyBold.headlineLarge,
            textAlign = TextAlign.Left,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TitlePreview() {
    Title("Ignition \nFinance")
}
