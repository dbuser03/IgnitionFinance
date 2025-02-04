package com.unimib.ignitionfinance.presentation.ui.screens

import BottomNavigationBarInstance
import android.app.Activity
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.unimib.ignitionfinance.R
import com.unimib.ignitionfinance.data.model.user.Product
import com.unimib.ignitionfinance.presentation.ui.components.CustomFAB
import com.unimib.ignitionfinance.presentation.ui.components.dialog.DialogManager
import com.unimib.ignitionfinance.presentation.ui.components.portfolio.DashboardCard
import com.unimib.ignitionfinance.presentation.ui.components.title.Title
import com.unimib.ignitionfinance.presentation.viewmodel.PortfolioScreenViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PortfolioScreen(
    navController: NavController,
    viewModel: PortfolioScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val dialogTitle = "Add your cash or product"
    var showDialog by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    var selectedCardIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(selectedCardIndex) {
        if (selectedCardIndex != -1) {
            listState.animateScrollToItem(selectedCardIndex)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getFirstAdded()
        viewModel.getCash()
        viewModel.getProducts()
    }

    BackHandler(enabled = true) {
        (context as? Activity)?.moveTaskToBack(true)
    }

    DialogManager(
        showDialog = showDialog,
        onDismissRequest = { showDialog = false },
        onConfirmation = { newCash ->
            showDialog = false
            newCash?.let {
                viewModel.updateCash(it)
            }
        },
        onProductConfirmation = { isin: String?, ticker: String?, purchaseDate: String?, amount: String?, symbol: String? ->
            showDialog = false
            if (isin != null && ticker != null && purchaseDate != null && amount != null && symbol != null) {
                val newProduct = Product(
                    isin = isin,
                    ticker = ticker,
                    purchaseDate = purchaseDate,
                    amount = amount,
                    symbol = "",
                    averagePerformance = "",
                    shares = 0.0,
                    currency = "",
                    historicalData = emptyMap()
                )
                viewModel.addNewProduct(newProduct)
            }
        },
        dialogTitle = dialogTitle,
        prefix = "€",
        firstAdded = state.isFirstAdded
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
                modifier = Modifier.padding(bottom = 12.dp),
                icon = painterResource(id = R.drawable.outline_add_24),
                contentDescription = stringResource(id = R.string.add_FAB_description)
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                state = listState
            ) {
                if (state.isFirstAdded) {
                    item {
                        DashboardCard(
                            modifier = Modifier,
                            isExpanded = state.expandedCardIndex == 0,
                            onCardClicked = {
                                viewModel.toggleCardExpansion(0)
                                selectedCardIndex = 0
                            },
                            isin = "BANK ACCOUNT",
                            ticker = "CASH",
                            isCash = true
                        )
                    }
                }

                itemsIndexed(state.products) { index, product ->
                    DashboardCard(
                        modifier = Modifier,
                        isExpanded = state.expandedCardIndex == index + if (state.isFirstAdded) 1 else 0,
                        onCardClicked = {
                            viewModel.toggleCardExpansion(index + if (state.isFirstAdded) 1 else 0)
                            selectedCardIndex = index
                        },
                        isin = product.isin,
                        product = product,
                        ticker = product.ticker,
                        isCash = false
                    )
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(88.dp)
                    )
                }
            }
        }
    )
}