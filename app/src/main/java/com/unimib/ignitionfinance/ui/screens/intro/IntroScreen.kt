package com.unimib.ignitionfinance.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.R // Make sure to import your R file
import com.unimib.ignitionfinance.ui.components.Background
import com.unimib.ignitionfinance.ui.components.Title
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme
import com.unimib.ignitionfinance.ui.theme.*

@Composable
fun IntroScreen() {
    Background {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // Title
            Title(
                title = "Ignition\nFinance",
                modifier = Modifier.padding(top = 16.dp)
            )

            // Spacer to separate elements
            Spacer(modifier = Modifier.height(24.dp))

            // Add the image with rounded corners
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) /*{
                Image(
                    painter = painterResource(id = R.drawable.image_back), // Replace with your image resource
                    contentDescription = "Intro Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                )
            }*/

            // Text Section
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Lorem ipsum dolors sit amet consectetur.\n" +
                        "Elementum quis tincidunt purus augue eu amet dignissim amet.",
                style = Typography.titleLarge,
                textAlign = TextAlign.Start
            )
        }
    }
}

// Add this for preview functionality
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun IntroScreenPreview() {
    IgnitionFinanceTheme {
        IntroScreen()
    }
}
