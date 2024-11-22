package com.unimib.ignitionfinance.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.unimib.ignitionfinance.ui.theme.*

@Composable
fun Title(
    title: String,
) {
    Text(
        text = title,
        style = TypographyBold.headlineLarge,
        textAlign = TextAlign.Left,
    )
}

@Preview(showBackground = true)
@Composable
fun TitlePreview() {
    Title("Ignition Finance")
}
