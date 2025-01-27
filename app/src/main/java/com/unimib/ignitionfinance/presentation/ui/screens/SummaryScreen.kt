package com.unimib.ignitionfinance.presentation.ui.screens

import BottomNavigationBarInstance
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.model.InputBoxModel
import com.unimib.ignitionfinance.presentation.ui.components.summary.NetworthDisplay
import com.unimib.ignitionfinance.presentation.ui.components.title.Title
import com.unimib.ignitionfinance.presentation.viewmodel.PortfolioScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.SummaryScreenViewModel

@Composable
fun SummaryScreen(
    navController: NavController,
    portfolioViewModel: PortfolioScreenViewModel = hiltViewModel(),
    summaryViewModel: SummaryScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    BackHandler(enabled = true) {
        (context as? Activity)?.moveTaskToBack(true)
    }
    Scaffold(
        topBar = {
            Title(title = stringResource(id = R.string.summary_title))
        },
        bottomBar = {
            BottomNavigationBarInstance(
                navController = navController
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NetworthDisplay(
                    inputBoxModel = InputBoxModel(
                        label = "Your net worth is:",
                        prefix = "€",
                        inputValue = remember{mutableStateOf(TextFieldValue("150,000"))},
                        key = "Networth",
                        iconResId = R.drawable.outline_person_apron_24
                    ),
                    portfolioScreenViewModel = portfolioViewModel,
                    summaryScreenViewModel = summaryViewModel
                )
            }
        }
    )
}

