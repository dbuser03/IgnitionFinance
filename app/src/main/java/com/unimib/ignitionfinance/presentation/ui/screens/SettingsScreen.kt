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
import com.unimib.ignitionfinance.presentation.model.SelectBoxModel
import com.unimib.ignitionfinance.presentation.ui.components.settings.CardItem
import com.unimib.ignitionfinance.presentation.viewmodel.SettingsScreenViewModel

@Composable
fun SettingsScreen(navController: NavController) {
    val settingsViewModel: SettingsScreenViewModel = hiltViewModel()

    val expandedCardIndex by remember { settingsViewModel.expandedCardIndex }
    val listState = rememberLazyListState()

    val inflationModel: SelectBoxModel by rememberUpdatedState(newValue = settingsViewModel.inflationModel)

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
                                            iconResId = R.drawable.outline_person_apron_24,
                                            inputValue = remember { mutableStateOf(TextFieldValue(settingsViewModel.withdrawalsWithoutPension.value)) }
                                        ),
                                        InputBoxModel(
                                            label = "Monthly withdrawals (with pension)",
                                            prefix = "€",
                                            iconResId = R.drawable.outline_person_4_24,
                                            inputValue = remember { mutableStateOf(TextFieldValue(settingsViewModel.withdrawalsWithPension.value)) }
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
                                        settingsViewModel.updateInflationModel(selectedText)
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
                                            iconResId = R.drawable.outline_account_balance_24,
                                            inputValue = remember { mutableStateOf(TextFieldValue(settingsViewModel.taxRatePercentage.value)) }
                                        ),
                                        InputBoxModel(
                                            label = "Stamp Duty Percentage",
                                            prefix = "%",
                                            iconResId = R.drawable.outline_position_top_right_24,
                                            inputValue = remember { mutableStateOf(TextFieldValue(settingsViewModel.stampDutyPercentage.value)) }
                                        ),
                                        InputBoxModel(
                                            label = "Load Percentage",
                                            prefix = "%",
                                            iconResId = R.drawable.outline_weight_24,
                                            inputValue = remember { mutableStateOf(TextFieldValue(settingsViewModel.loadPercentage.value)) }
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
                                            iconResId = R.drawable.outline_local_fire_department_24,
                                            inputValue = remember { mutableStateOf(TextFieldValue(settingsViewModel.yearsInFIRE.value)) }
                                        ),
                                        InputBoxModel(
                                            label = "Years in paid retirement",
                                            prefix = "YRS",
                                            iconResId = R.drawable.outline_send_money_24,
                                            inputValue = remember { mutableStateOf(TextFieldValue(settingsViewModel.yearsInPaidRetirement.value)) }
                                        ),
                                        InputBoxModel(
                                            label = "Years of buffer",
                                            prefix = "YRS",
                                            iconResId = R.drawable.outline_clock_loader_10_24,
                                            inputValue = remember { mutableStateOf(TextFieldValue(settingsViewModel.yearsOfBuffer.value)) }
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
                                            iconResId = R.drawable.outline_autoplay_24,
                                            inputValue = remember { mutableStateOf(TextFieldValue(settingsViewModel.numberOfSimulations.value.toString())) }
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