package com.unimib.ignitionfinance.presentation.ui.screens

import BottomNavigationBarInstance
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.unimib.ignitionfinance.presentation.ui.components.portfolio.ProductCard
import com.unimib.ignitionfinance.presentation.ui.components.settings.CardItem
import com.unimib.ignitionfinance.presentation.ui.components.title.Title
import com.unimib.ignitionfinance.presentation.viewmodel.PortfolioScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState

@Composable
fun PortfolioScreen(
    navController: NavController,
    viewModel: PortfolioScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val dialogTitle = "Add your cash"
    var showDialog by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()

    val listState = rememberLazyListState()

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
        onProductConfirmation = { isin, ticker, purchaseDate, amount ->
            showDialog = false
            if (isin != null && ticker != null && purchaseDate != null && amount != null) {
                val newProduct = Product(
                    isin = isin,
                    ticker = ticker,
                    purchaseDate = purchaseDate,
                    amount = amount
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
                modifier = Modifier
                    .padding(bottom = 12.dp),
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
                when (state.productsState) {
                    is UiState.Success -> {
                        itemsIndexed(
                            items = buildList {
                                if (state.cash.toDouble() > 0) {
                                    add(Product(
                                        isin = "BANK ACCOUNT",
                                        ticker = "CASH",
                                        purchaseDate = null,
                                        amount = state.cash
                                    ))
                                }
                                addAll(state.products.reversed())
                            }
                        ) { index, product ->
                            CardItem(
                                cardIndex = index,
                                expandedCardIndex = state.expandedCardIndex,
                                listState = listState
                            ) {
                                ProductCard(
                                    modifier = Modifier,
                                    isExpanded = state.expandedCardIndex == index,
                                    onCardClicked = {
                                        viewModel.toggleCardExpansion(index)
                                    },
                                    viewModel = viewModel,
                                    isin = product.isin,
                                    ticker = product.ticker,
                                    isCash = product.ticker == "CASH"
                                )
                            }
                        }
                    }
                    is UiState.Error -> {
                        // Handle error state if needed
                    }
                    is UiState.Loading -> {
                        // Show loading indicator if needed
                    }
                    else -> { /* Handle other states */ }
                }
            }
        }
    )
}