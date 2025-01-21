package com.unimib.ignitionfinance.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.unimib.ignitionfinance.presentation.ui.components.title.TitleSettings
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.components.settings.input.InputCard
import com.unimib.ignitionfinance.presentation.ui.components.settings.select.SelectCard
import com.unimib.ignitionfinance.presentation.model.InputBoxModel
import com.unimib.ignitionfinance.presentation.ui.components.settings.CardItem
import com.unimib.ignitionfinance.presentation.model.SelectBoxModel
import com.unimib.ignitionfinance.presentation.viewmodel.SettingsScreenViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsScreenViewModel = hiltViewModel()
) {
    val expandedCardIndex by remember { settingsViewModel.expandedCardIndex }
    val listState = rememberLazyListState()
    val settings = settingsViewModel.settings.value

    val settingsData = settings ?: settingsViewModel.getDefaultSettings()

    var inflationModel by remember {
        mutableStateOf(
            SelectBoxModel(
                key = "inflationModel",
                text = "Choose the inflation model:",
                displayedTexts = listOf("NORMAL", "SCALE", "LOGNORMAL"),
                selectedText = settingsData.inflationModel
            )
        )
    }

    LaunchedEffect(Unit) {
        settingsViewModel.getUserSettings()
    }

    LaunchedEffect(settings) {
        settings?.let {
            inflationModel = inflationModel.copy(selectedText = it.inflationModel)
        }
    }

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
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        CardItem(
                            cardIndex = 0,
                            expandedCardIndex = expandedCardIndex,
                            listState = listState,
                            content = {
                                InputCard(
                                    label = "NORMAL, RETIREMENT",
                                    title = "WITHDRAW",
                                    inputBoxModelList = listOf(
                                        InputBoxModel(
                                            key = "monthlyWithdrawalsWithoutPension",
                                            label = "Monthly withdrawals (no pension)",
                                            prefix = "€",
                                            iconResId = R.drawable.outline_person_apron_24,
                                            inputValue = remember(settings) {
                                                mutableStateOf(TextFieldValue(settingsData.withdrawals.withoutPension))
                                            }
                                        ),
                                        InputBoxModel(
                                            key = "monthlyWithdrawalsWithPension",
                                            label = "Monthly withdrawals (with pension)",
                                            prefix = "€",
                                            iconResId = R.drawable.outline_person_4_24,
                                            inputValue = remember(settings) {
                                                mutableStateOf(TextFieldValue(settingsData.withdrawals.withPension))
                                            }
                                        )
                                    ),
                                    isExpanded = expandedCardIndex == 0,
                                    onCardClicked = { settingsViewModel.toggleCardExpansion(0) }
                                )
                            }
                        )
                    }
                    item {
                        CardItem(
                            cardIndex = 1,
                            expandedCardIndex = expandedCardIndex,
                            listState = listState,
                            content = {
                                SelectCard(
                                    label = "NORMAL, SCALE, LOGNORMAL",
                                    title = "INFLATION",
                                    model = inflationModel,
                                    isExpanded = expandedCardIndex == 1,
                                    onCardClicked = { settingsViewModel.toggleCardExpansion(1) },
                                    onTextSelected = { selectedText ->
                                        inflationModel = inflationModel.copy(selectedText = selectedText)
                                        settings?.let { currentSettings ->
                                            val updatedSettings = currentSettings.copy(
                                                inflationModel = selectedText
                                            )
                                            settingsViewModel.updateSettings(updatedSettings)
                                        }
                                    }
                                )
                            }
                        )
                    }
                    item {
                        CardItem(
                            cardIndex = 2,
                            expandedCardIndex = expandedCardIndex,
                            listState = listState,
                            content = {
                                InputCard(
                                    label = "TAX RATE, STAMP DUTY, LOAD",
                                    title = "EXPENSES",
                                    inputBoxModelList = listOf(
                                        InputBoxModel(
                                            key = "taxRatePercentage",
                                            label = "Tax Rate Percentage",
                                            prefix = "%",
                                            iconResId = R.drawable.outline_account_balance_24,
                                            inputValue = remember(settings) {
                                                mutableStateOf(TextFieldValue(settingsData.expenses.taxRatePercentage))
                                            }
                                        ),
                                        InputBoxModel(
                                            key = "stampDutyPercentage",
                                            label = "Stamp Duty Percentage",
                                            prefix = "%",
                                            iconResId = R.drawable.outline_position_top_right_24,
                                            inputValue = remember(settings) {
                                                mutableStateOf(TextFieldValue(settingsData.expenses.stampDutyPercentage))
                                            }
                                        ),
                                        InputBoxModel(
                                            key = "loadPercentage",
                                            label = "Load Percentage",
                                            prefix = "%",
                                            iconResId = R.drawable.outline_weight_24,
                                            inputValue = remember(settings) {
                                                mutableStateOf(TextFieldValue(settingsData.expenses.loadPercentage))
                                            }
                                        )
                                    ),
                                    isExpanded = expandedCardIndex == 2,
                                    onCardClicked = { settingsViewModel.toggleCardExpansion(2) }
                                )
                            }
                        )
                    }
                    item {
                        CardItem(
                            cardIndex = 3,
                            expandedCardIndex = expandedCardIndex,
                            listState = listState,
                            content = {
                                InputCard(
                                    label = "YEARS, RETIREMENTS YEARS, BUFFER",
                                    title = "INTERVALS",
                                    inputBoxModelList = listOf(
                                        InputBoxModel(
                                            key = "yearsInFire",
                                            label = "Years in FIRE",
                                            prefix = "YRS",
                                            iconResId = R.drawable.outline_local_fire_department_24,
                                            inputValue = remember(settings) {
                                                mutableStateOf(TextFieldValue(settingsData.intervals.yearsInFIRE))
                                            }
                                        ),
                                        InputBoxModel(
                                            key = "yearsInPaidRetirement",
                                            label = "Years in paid retirement",
                                            prefix = "YRS",
                                            iconResId = R.drawable.outline_send_money_24,
                                            inputValue = remember(settings) {
                                                mutableStateOf(TextFieldValue(settingsData.intervals.yearsInPaidRetirement))
                                            }
                                        ),
                                        InputBoxModel(
                                            key = "yearsOfBuffer",
                                            label = "Years of buffer",
                                            prefix = "YRS",
                                            iconResId = R.drawable.outline_clock_loader_10_24,
                                            inputValue = remember(settings) {
                                                mutableStateOf(TextFieldValue(settingsData.intervals.yearsOfBuffer))
                                            }
                                        )
                                    ),
                                    isExpanded = expandedCardIndex == 3,
                                    onCardClicked = { settingsViewModel.toggleCardExpansion(3) }
                                )
                            }
                        )
                    }
                    item {
                        CardItem(
                            cardIndex = 4,
                            expandedCardIndex = expandedCardIndex,
                            listState = listState,
                            content = {
                                InputCard(
                                    label = "NUMBER",
                                    title = "SIMULATIONS",
                                    inputBoxModelList = listOf(
                                        InputBoxModel(
                                            key = "numberOfSimulations",
                                            label = "Number of simulations to perform",
                                            prefix = "N°",
                                            iconResId = R.drawable.outline_autoplay_24,
                                            inputValue = remember(settings) {
                                                mutableStateOf(TextFieldValue(settingsData.numberOfSimulations))
                                            }
                                        )
                                    ),
                                    isExpanded = expandedCardIndex == 4,
                                    onCardClicked = { settingsViewModel.toggleCardExpansion(4) }
                                )
                            }
                        )
                    }
                }
            }
        }
    )
}