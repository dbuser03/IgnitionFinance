package com.unimib.ignitionfinance.presentation.ui.screens

import BottomNavigationBarInstance
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.components.title.Title
import com.unimib.ignitionfinance.presentation.ui.theme.IgnitionFinanceTheme

@Composable
fun SummaryScreen(navController: NavController) {
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

            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SummaryScreenPreview() {
    IgnitionFinanceTheme {
        val navController = rememberNavController()
        SummaryScreen(navController = navController)
    }
}