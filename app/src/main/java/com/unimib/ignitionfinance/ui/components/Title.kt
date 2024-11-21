package com.unimib.ignitionfinance.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.ui.theme.*

@Composable
fun Title(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = TypographyBold.headlineLarge, // Adjust to use your desired font style
        textAlign = TextAlign.Left,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun TitlePreview() {
    Title("Ignition Finance")
}
