package com.unimib.ignitionfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        contentAlignment = Alignment.Center
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

@Composable
fun TitleWithDescription(
    title: String,
    description: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(216.dp)
            .background(color = BackgroundWhite)
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
                .align(Alignment.Center)
        )
        Text(
            text = description,
            style = Typography.bodyMedium,
            textAlign = TextAlign.Left,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
                .align(Alignment.BottomStart)
                .padding(bottom = 8.dp)
        )
    }
}

@Composable
fun TitleWithDescriptionAndCircle(
    title: String,
    description: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(216.dp)
            .background(color = BackgroundWhite)
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
                .align(Alignment.CenterStart)
        )
        Text(
            text = description,
            style = Typography.bodyMedium,
            textAlign = TextAlign.Left,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
                .align(Alignment.BottomStart)
                .padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color = Color.Blue, shape = CircleShape)
                .align(Alignment.CenterEnd)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TitlePreview() {
    Title("Ignition \nFinance")
}

@Preview(showBackground = true)
@Composable
fun TitleWithDescriptionPreview() {
    TitleWithDescription(
        title = "Ignition \nFinance",
        description = "Manage your finances easily and efficiently with our powerful tools."
    )
}

@Preview(showBackground = true)
@Composable
fun TitleWithDescriptionAndCirclePreview() {
    TitleWithDescriptionAndCircle(
        title = "Ignition \nFinance",
        description = "Manage your finances easily and efficiently with our powerful tools."
    )
}
