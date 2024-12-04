@file:Suppress("DEPRECATION")

package com.unimib.ignitionfinance.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.navigation.Destinations
import com.unimib.ignitionfinance.presentation.ui.theme.TypographyBold


@Composable
fun Title(
    title: String,
    color: Color = MaterialTheme.colorScheme.primary
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
                .align(Alignment.CenterStart)
                .padding(start = 16.dp, end = 24.dp)
        )
    }
}

@Composable
fun TitleWithDescription(
    title: String,
    description: String,
    color: Color = MaterialTheme.colorScheme.primary
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
                .padding(start = 16.dp, end = 24.dp)
                .align(Alignment.CenterStart)
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
                .padding(start = 16.dp, end = 24.dp)
                .align(Alignment.BottomStart)
        )
    }
}

@Composable
fun TitleWithButton(
    title: String,
    description: String,
    navController: NavController,
    color: Color = MaterialTheme.colorScheme.primary
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
                .padding(start = 16.dp, end = 24.dp)
                .align(Alignment.CenterStart)
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
                .padding(start = 16.dp, end = 24.dp)
                .align(Alignment.BottomStart)
        )

        CustomFloatingActionButton(
            onClick = { navController.navigate(Destinations.SettingsScreen.route) },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 24.dp),
            containerColor = MaterialTheme.colorScheme.onSecondary,
            contentColor = MaterialTheme.colorScheme.primary,
            icon = painterResource(id = R.drawable.outline_settings_24),
            contentDescription = stringResource(id = R.string.settings_FAB_description),
            fabSize = 40.dp,
            iconSize = 24.dp,
            hapticFeedbackType = HapticFeedbackType.TextHandleMove
        )
    }
}

@Composable
fun TitleSettings(
    title: String,
    color: Color = MaterialTheme.colorScheme.primary,
    navController: NavController
) {
    // Get the HapticFeedback instance
    val hapticFeedback = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(256.dp)
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = {
                // Perform haptic feedback on click
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                // Pop back stack navigation
                navController.popBackStack()
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 72.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_arrow_back_24),
                contentDescription = stringResource(id = R.string.go_back_FAB_description),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }

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
                .align(Alignment.CenterStart)
                .padding(start = 16.dp, end = 24.dp, top = 64.dp)
        )
    }
}
