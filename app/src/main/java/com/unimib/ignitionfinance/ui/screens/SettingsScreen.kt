package com.unimib.ignitionfinance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.ui.components.TitleSettings
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.ui.components.ExpandableInputCard
import com.unimib.ignitionfinance.ui.components.ExpandableSelectCard

@Composable
fun SettingsScreen(navController: NavController) {
    var expandedCardIndex by remember { mutableStateOf(-1) }
    val listState = rememberLazyListState()

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
                        ExpandableCardItem(
                            cardIndex = 0,
                            expandedCardIndex = expandedCardIndex,
                            listState = listState,
                            content = {
                                ExpandableInputCard(
                                    label = "NORMAL, RETIREMENT",
                                    title = "WITHDRAW",
                                    inputValues = List(2) { remember { mutableStateOf(TextFieldValue("----")) } },
                                    prefixes = listOf("€", "€"),
                                    iconResIds = listOf(R.drawable.outline_person_apron_24, R.drawable.outline_person_4_24),
                                    inputBoxes = listOf("Monthly withdrawals (no pension)", "Monthly withdrawals (with pension)"),
                                    isExpanded = expandedCardIndex == 0,
                                    onCardClicked = { expandedCardIndex = toggleCardExpansion(expandedCardIndex, 0) }
                                )
                            }
                        )
                    }
                    item {
                        ExpandableCardItem(
                            cardIndex = 1,
                            expandedCardIndex = expandedCardIndex,
                            listState = listState,
                            content = {
                                ExpandableSelectCard(
                                    label = "NORMAL, SCALE, LOGNORMAL",
                                    title = "INFLATION",
                                    inputText = "Choose the inflation model:",
                                    displayedTexts = listOf("NORMAL", "SCALE", "LOGNORMAL"),
                                    initialSelectedText = "SCALE",
                                    isExpanded = expandedCardIndex == 1,
                                    onCardClicked = { expandedCardIndex = toggleCardExpansion(expandedCardIndex, 1) }
                                )
                            }
                        )
                    }
                    item {
                        ExpandableCardItem(
                            cardIndex = 2,
                            expandedCardIndex = expandedCardIndex,
                            listState = listState,
                            content = {
                                ExpandableInputCard(
                                    label = "TAX RATE, STAMP DUTY, LOAD",
                                    title = "EXPENSES",
                                    inputValues = List(3) { remember { mutableStateOf(TextFieldValue("----")) } },
                                    prefixes = listOf("%", "%", "%"),
                                    iconResIds = listOf(R.drawable.outline_account_balance_24, R.drawable.outline_position_top_right_24, R.drawable.outline_weight_24),
                                    inputBoxes = listOf("Tax Rate Percentage", "Stamp Duty Percentage", "Load Percentage"),
                                    isExpanded = expandedCardIndex == 2,
                                    onCardClicked = { expandedCardIndex = toggleCardExpansion(expandedCardIndex, 2) }
                                )
                            }
                        )
                    }
                    item {
                        ExpandableCardItem(
                            cardIndex = 3,
                            expandedCardIndex = expandedCardIndex,
                            listState = listState,
                            content = {
                                ExpandableInputCard(
                                    label = "YEARS, RETIREMENTS YEARS, BUFFER",
                                    title = "INTERVALS",
                                    inputValues = List(3) { remember { mutableStateOf(TextFieldValue("----")) } },
                                    prefixes = listOf("YRS", "YRS", "YRS"),
                                    iconResIds = listOf(R.drawable.outline_local_fire_department_24, R.drawable.outline_send_money_24, R.drawable.outline_clock_loader_10_24),
                                    inputBoxes = listOf("Years in FIRE", "Years in paid retirement", "Years of buffer"),
                                    isExpanded = expandedCardIndex == 3,
                                    onCardClicked = { expandedCardIndex = toggleCardExpansion(expandedCardIndex, 3) }
                                )
                            }
                        )
                    }
                    item {
                        ExpandableCardItem(
                            cardIndex = 4,
                            expandedCardIndex = expandedCardIndex,
                            listState = listState,
                            content = {
                                ExpandableInputCard(
                                    label = "NUMBER",
                                    title = "SIMULATIONS",
                                    inputValues = List(1) { remember { mutableStateOf(TextFieldValue("----")) } },
                                    prefixes = listOf("N°"),
                                    iconResIds = listOf(R.drawable.outline_autoplay_24),
                                    inputBoxes = listOf("Number of simulations to perform"),
                                    isExpanded = expandedCardIndex == 4,
                                    onCardClicked = { expandedCardIndex = toggleCardExpansion(expandedCardIndex, 4) }
                                )
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun ExpandableCardItem(
    cardIndex: Int,
    expandedCardIndex: Int,
    listState: LazyListState,
    content: @Composable () -> Unit
) {
    if (expandedCardIndex == cardIndex) {
        LaunchedEffect(cardIndex) {
            listState.animateScrollToItem(cardIndex)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        content()
    }
}

fun toggleCardExpansion(expandedCardIndex: Int, cardIndex: Int): Int {
    return if (expandedCardIndex == cardIndex) -1 else cardIndex
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    IgnitionFinanceTheme {
        val navController = rememberNavController()
        SettingsScreen(navController = navController)
    }
}
