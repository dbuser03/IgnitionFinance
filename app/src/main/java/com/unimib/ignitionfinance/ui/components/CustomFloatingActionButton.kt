package com.unimib.ignitionfinance.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme
import com.unimib.ignitionfinance.ui.theme.PrimaryBlack
import com.unimib.ignitionfinance.ui.theme.PrimaryWhite
import com.unimib.ignitionfinance.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp

@Composable
fun CustomFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    icon: Painter,
    contentDescription: String? = null,
    fabSize: Dp = 56.dp,
    iconSize: Dp = 24.dp,
    isClickable: Boolean = true
) {
    val hapticFeedback = LocalHapticFeedback.current

    val feedbackType = if (fabSize < 56.dp) {
        HapticFeedbackType.TextHandleMove
    } else {
        HapticFeedbackType.LongPress
    }

    FloatingActionButton(
        onClick = {
            if (isClickable) {
                hapticFeedback.performHapticFeedback(feedbackType)
                onClick()
            }
        },
        modifier = modifier.size(fabSize),
        containerColor = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.medium.copy(CornerSize(50)),
        elevation = FloatingActionButtonDefaults.elevation(0.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize)
        )
    }
}


@Preview
@Composable
fun AddFABPreview() {
    IgnitionFinanceTheme {
        CustomFloatingActionButton(
            onClick = {},
            icon = painterResource(id = R.drawable.outline_add_24),
            contentDescription = stringResource(id = R.string.add_FAB_description)
        )
    }
}

@Preview
@Composable
fun SwipeUpFABPreview() {
    IgnitionFinanceTheme {
        CustomFloatingActionButton(
            onClick = { /* Handle click action */ },
            modifier = Modifier,
            containerColor = PrimaryWhite,
            contentColor = PrimaryBlack,
            icon = painterResource(id = R.drawable.outline_keyboard_arrow_up_24),
            contentDescription = stringResource(id = R.string.swipe_up_FAB_description)
        )
    }
}

@Preview
@Composable
fun SettingsFABPreview() {
    IgnitionFinanceTheme {
        CustomFloatingActionButton(
            onClick = { /* Handle click action */ },
            modifier = Modifier,
            containerColor = MaterialTheme.colorScheme.onSecondary,
            contentColor = MaterialTheme.colorScheme.primary,
            icon = painterResource(id = R.drawable.outline_settings_24),
            contentDescription = stringResource(id = R.string.settings_FAB_description),
            fabSize = 40.dp,
            iconSize = 24.dp
        )
    }
}

@Preview
@Composable
fun GoBackFABPreview() {
    IgnitionFinanceTheme {
        CustomFloatingActionButton(
            onClick = { /* Handle click action */ },
            modifier = Modifier,
            containerColor = Color.Transparent,
            icon = painterResource(id = R.drawable.outline_arrow_back_24),
            contentDescription = stringResource(id = R.string.go_back_FAB_description),
            fabSize = 32.dp,
            iconSize = 32.dp
        )
    }
}
