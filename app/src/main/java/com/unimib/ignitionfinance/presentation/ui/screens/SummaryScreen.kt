package com.unimib.ignitionfinance.presentation.ui.screens

import BottomNavigationBarInstance
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.model.InputBoxModel
import com.unimib.ignitionfinance.presentation.ui.components.summary.AssetAllocationCard
import com.unimib.ignitionfinance.presentation.ui.components.summary.NetWorthDisplay
import com.unimib.ignitionfinance.presentation.ui.components.title.Title
import com.unimib.ignitionfinance.presentation.viewmodel.PortfolioScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.SummaryScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import kotlinx.coroutines.delay

@Composable
fun SummaryScreen(
    navController: NavController,
    portfolioViewModel: PortfolioScreenViewModel = hiltViewModel(),
    summaryViewModel: SummaryScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val portfolioState = portfolioViewModel.state.collectAsState().value
    val summaryState = summaryViewModel.state.collectAsState().value
    val isNetWorthHidden = summaryState.isNetWorthHidden

    val cash = remember { mutableDoubleStateOf(0.0) }
    val invested = remember { mutableDoubleStateOf(0.0) }
    val isLoading = remember { mutableStateOf(true) }
    val valuesCalculated = remember { mutableStateOf(false) }
    var showAssetCard by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        portfolioViewModel.getCash()
        summaryViewModel.getInvested()
    }

    LaunchedEffect(portfolioState.cashState, summaryState.investedState) {
        isLoading.value = when {
            portfolioState.cashState is UiState.Loading ||
                    summaryState.investedState is UiState.Loading -> true

            portfolioState.cashState is UiState.Success &&
                    summaryState.investedState is UiState.Success -> {
                val cashString = portfolioState.cashState.data
                val cleanCashString = cashString.replace("[^0-9.]".toRegex(), "")
                cash.doubleValue = cleanCashString.toDoubleOrNull() ?: 0.0

                invested.doubleValue = summaryState.investedState.data
                valuesCalculated.value = true
                false
            }
            else -> false
        }
    }

    LaunchedEffect(valuesCalculated.value) {
        if (valuesCalculated.value) {
            delay(300)
            showAssetCard = true
        }
    }

    BackHandler(enabled = true) {
        (context as? Activity)?.moveTaskToBack(true)
    }

    Scaffold(
        topBar = {
            Title(title = stringResource(id = R.string.summary_title))
        },
        bottomBar = {
            BottomNavigationBarInstance(navController = navController)
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NetWorthDisplay(
                    inputBoxModel = InputBoxModel(
                        label = "Your net worth is:",
                        prefix = "€",
                        inputValue = remember { mutableStateOf(TextFieldValue("")) },
                        key = "NetWorth",
                        iconResId = R.drawable.outline_person_apron_24
                    ),
                    cash = cash.doubleValue,
                    invested = invested.doubleValue,
                    isLoading = isLoading.value,
                    isNetWorthHidden = isNetWorthHidden,
                    onVisibilityToggle = { summaryViewModel.toggleNetWorthVisibility() }
                )

                AnimatedVisibility(
                    visible = showAssetCard,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 500,
                            delayMillis = 0
                        )
                    )
                ) {
                    AssetAllocationCard(
                        cash = cash.doubleValue,
                        invested = invested.doubleValue
                    )
                }
            }
        }
    )
}