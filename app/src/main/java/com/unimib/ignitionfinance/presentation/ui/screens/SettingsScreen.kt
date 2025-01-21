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
                                            label = "Monthly withdrawals (no pension)",
                                            prefix = "€",
                                            inputValue = remember(settings) {
                                                mutableStateOf(TextFieldValue(settingsData.withdrawals.withoutPension))
                                            }
                                        ),
                                        InputBoxModel(
                                            label = "Monthly withdrawals (with pension)",
                                            prefix = "€",
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
                                            label = "Tax Rate Percentage",
                                            prefix = "%",
                                            inputValue = remember(settings) {
                                                mutableStateOf(TextFieldValue(settingsData.expenses.taxRatePercentage))
                                            }
                                        ),
                                        InputBoxModel(
                                            label = "Stamp Duty Percentage",
                                            prefix = "%",
                                            inputValue = remember(settings) {
                                                mutableStateOf(TextFieldValue(settingsData.expenses.stampDutyPercentage))
                                            }
                                        ),
                                        InputBoxModel(
                                            label = "Load Percentage",
                                            prefix = "%",
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
                                            label = "Years in FIRE",
                                            prefix = "YRS",
                                            inputValue = remember(settings) {
                                                mutableStateOf(TextFieldValue(settingsData.intervals.yearsInFIRE))
                                            }
                                        ),
                                        InputBoxModel(
                                            label = "Years in paid retirement",
                                            prefix = "YRS",
                                            inputValue = remember(settings) {
                                                mutableStateOf(TextFieldValue(settingsData.intervals.yearsInPaidRetirement))
                                            }
                                        ),
                                        InputBoxModel(
                                            label = "Years of buffer",
                                            prefix = "YRS",
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
                                            label = "Number of simulations to perform",
                                            prefix = "N°",
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