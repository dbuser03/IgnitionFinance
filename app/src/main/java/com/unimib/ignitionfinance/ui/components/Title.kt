@file:Suppress("DEPRECATION")
package com.unimib.ignitionfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.ui.theme.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.unimib.ignitionfinance.R

@Composable
fun Title(
    title: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(216.dp)
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = color,
            style = MaterialTheme.typography.headlineLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = TypographyBold.headlineLarge.fontWeight
            ),
            textAlign = TextAlign.Left,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
                .padding(end = 24.dp)
                .padding(top = 16.dp)
        )
    }
}

@Composable
fun TitleWithDescription(
    title: String,
    description: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(216.dp)
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = title,
            color = color,
            style = MaterialTheme.typography.headlineLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = TypographyBold.headlineLarge.fontWeight
            ),
            textAlign = TextAlign.Left,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
                .align(Alignment.Center)
                .padding(end = 24.dp)
        )
        Text(
            text = description,
            color = color,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Left,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
                .align(Alignment.BottomStart)
                .padding(end = 24.dp)
        )
    }
}

@Composable
fun TitleWithDescriptionAndButton(
    title: String,
    description: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(216.dp)
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = title,
            color = color,
            style = MaterialTheme.typography.headlineLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = TypographyBold.headlineLarge.fontWeight
            ),
            textAlign = TextAlign.Left,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
                .align(Alignment.CenterStart)
                .padding(end = 24.dp)
        )
        Text(
            text = description,
            color = color,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Left,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
                .align(Alignment.BottomStart)
                .padding(end = 24.dp)
        )

        CustomFloatingActionButton(
            onClick = { /* Handle click action */ },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 24.dp),
            containerColor = PrimaryWhite,
            contentColor = PrimaryBlack,
            icon = painterResource(id = R.drawable.outline_settings_24),
            contentDescription = stringResource(id = R.string.settings_FAB_description),
            fabSize = 40.dp,
            iconSize = 24.dp
        )
    }
}

@Composable
fun TitleSettings(
    title: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(256.dp)
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        GoBackIcon(
            icon = painterResource(id = R.drawable.outline_arrow_back_24),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp)
                .padding(top = 72.dp)
        )
        Text(
            text = title,
            color = color,
            style = MaterialTheme.typography.headlineLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = TypographyBold.headlineLarge.fontWeight
            ),
            textAlign = TextAlign.Left,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
                .padding(end = 24.dp)
                .padding(top = 48.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TitlePreview() {
    IgnitionFinanceTheme {
        Title("Ignition \nFinance")
    }
}

@Preview(showBackground = true)
@Composable
fun TitleWithDescriptionPreview() {
    IgnitionFinanceTheme {
        TitleWithDescription(
            title = "Your \nNet Worth",
            description = "Manage your finances easily and efficiently with our powerful tools."
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TitleWithDescriptionAndIconPreview() {
    IgnitionFinanceTheme {
        TitleWithDescriptionAndButton(
            title = "FIRE \nSimulation",
            description = "Manage your finances easily and efficiently with our powerful tools."
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TitleSettingsPreview() {
    IgnitionFinanceTheme {
        TitleSettings("Settings")
    }
}
