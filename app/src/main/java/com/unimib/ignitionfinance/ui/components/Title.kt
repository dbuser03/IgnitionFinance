@file:Suppress("DEPRECATION")
package com.unimib.ignitionfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.navigation.compose.rememberNavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.ui.navigation.Destinations
import androidx.navigation.NavController


@Composable
fun Title(
    title: String,
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
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .padding(end = 24.dp)
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
    }
}

@Composable
fun TitleWithButton(
    title: String,
    description: String,
    navController: NavController, // Aggiungi il parametro navController
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
            onClick = { navController.navigate(Destinations.SettingsScreen.route) }, // Naviga alla SettingsScreen
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
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    navController: NavController // Necessario per la navigazione
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
                .padding(top = 72.dp),
            onClick = { navController.navigate(Destinations.SimulationScreen.route) } // Naviga alla SimulationScreen
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
                .align(Alignment.CenterStart)
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
        // Crea un NavController per la preview
        val navController = rememberNavController()

        // Passa il NavController alla composizione
        TitleWithButton(
            title = "FIRE \nSimulation",
            description = "Manage your finances easily and efficiently with our powerful tools.",
            navController = navController // Aggiungi il NavController
        )
    }
}


@Preview(showBackground = true)
@Composable
fun TitleSettingsPreview() {
    IgnitionFinanceTheme {
        val navController = rememberNavController() // Crea un NavController simulato
        TitleSettings(
            title = "Settings",
            navController = navController
        )
    }
}
