package com.unimib.ignitionfinance.presentation.ui.screens

import BottomNavigationBarInstance
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.presentation.ui.components.CustomFAB
import com.unimib.ignitionfinance.presentation.ui.components.dialog.DialogManager
import com.unimib.ignitionfinance.presentation.ui.components.title.Title

@Composable
fun PortfolioScreen(navController: NavController) {
    val context = LocalContext.current
    val dialogTitle = "Add your cash"
    var showDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        (context as? Activity)?.moveTaskToBack(true)
    }

    DialogManager(
        showDialog = showDialog,
        onDismissRequest = { showDialog = false },
        onConfirmation = { showDialog = false },
        // Update dati su db
        // Aggiorno prima il model -> in data/model
        // Aggiorno entity, mapper, aggiorna versione db = 5
        // Aggiorno la sezione remote -> aggiorna UserDataMapper
        // Da console firestore aggiungi un campo cash "string"
        // Refactoring package use case -> raggruppa in un package gli use case settings, auth
        // Scrivi lo use case GetUserCashUseCase
        // Scrivi lo use case UpdateUserCashUseCase
        // Modifichi il view model della portfolio screen per fare chiamate get e update
        // Qui su portfolio screen fai in modo che si apra il dialog cash solo la prima volta che clicco il FAB -> per testarlo fai più click e assicurati che non si apra
        dialogTitle = dialogTitle,
        prefix = "€",
    )

    Scaffold(
        topBar = {
            Title(title = stringResource(id = R.string.portfolio_title))
        },
        bottomBar = {
            BottomNavigationBarInstance(
                navController = navController
            )
        },
        floatingActionButton = {
            CustomFAB(
                onClick = { showDialog = true },
                modifier = Modifier
                    .padding(bottom = 12.dp),
                icon = painterResource(id = R.drawable.outline_add_24),
                contentDescription = stringResource(id = R.string.add_FAB_description)
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    )
}