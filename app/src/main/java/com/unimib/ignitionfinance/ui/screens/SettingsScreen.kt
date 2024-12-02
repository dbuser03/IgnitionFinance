package com.unimib.ignitionfinance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unimib.ignitionfinance.ui.components.TitleSettings
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.ui.components.ExpandableInputCard
import com.unimib.ignitionfinance.ui.components.ExpandableSelectCard

@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TitleSettings(
                title = stringResource(id = R.string.settings_title),
                navController = navController
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    ExpandableInputCard(
                        label = "NORMAL, RETIREMENT",
                        title = "WITHDRAW",
                        initiallyExpanded = false,
                        inputValues = List(2) { remember { mutableStateOf(TextFieldValue("----")) } },
                        prefixes = listOf("€", "€"),
                        iconResIds = listOf(R.drawable.outline_person_apron_24, R.drawable.outline_person_4_24),
                        inputBoxes = listOf("Monthly withdrawals (no pension)", "Monthly withdrawals (with pension)")
                    )
                    ExpandableSelectCard(
                        label = "NORMAL, SCALE, LOGNORMAL",
                        title = "INFLATION",
                        inputText = "Choose the inflation model",
                        initiallyExpanded = false,
                        displayedTexts = listOf("NORMAL", "SCALE", "LOGNORMAL"),
                        initialSelectedText = "SCALE"
                    )
                    ExpandableInputCard(
                        label = "TAX RATE, STAMP DUTY, LOAD",
                        title = "EXPENSES",
                        initiallyExpanded = false,
                        inputValues = List(3) { remember { mutableStateOf(TextFieldValue("----")) } },
                        prefixes = listOf("%", "%", "%"),
                        iconResIds = listOf(R.drawable.outline_account_balance_24, R.drawable.outline_position_top_right_24, R.drawable.outline_weight_24),
                        inputBoxes = listOf("Tax Rate Percentage", "Stamp Duty Percentage", "Load Percentage")
                    )
                    ExpandableInputCard(
                        label = "YEARS, RETIREMENTS YEARS, BUFFER",
                        title = "INTERVALS",
                        initiallyExpanded = false,
                        inputValues = List(3) { remember { mutableStateOf(TextFieldValue("----")) } },
                        prefixes = listOf("YRS", "YRS", "YRS"),
                        iconResIds = listOf(R.drawable.outline_local_fire_department_24, R.drawable.outline_send_money_24, R.drawable.outline_clock_loader_10_24),
                        inputBoxes = listOf("Years in FIRE", "Years in paid retirement", "Years of buffer")
                    )
                    ExpandableInputCard(
                        label = "NUMBER",
                        title = "SIMULATIONS",
                        initiallyExpanded = false,
                        inputValues = List(3) { remember { mutableStateOf(TextFieldValue("----")) } },
                        prefixes = listOf("N°"),
                        iconResIds = listOf(R.drawable.outline_autoplay_24),
                        inputBoxes = listOf("Number of simulations to perform")
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    IgnitionFinanceTheme {
        val navController = rememberNavController()
        SettingsScreen(navController = navController)
    }
}
