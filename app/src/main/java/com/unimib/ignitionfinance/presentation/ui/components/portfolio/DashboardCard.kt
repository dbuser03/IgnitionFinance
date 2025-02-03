package com.unimib.ignitionfinance.presentation.ui.components.portfolio

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unimib.ignitionfinance.data.model.user.Product
import com.unimib.ignitionfinance.presentation.ui.components.settings.input.InputCardHeader
import com.unimib.ignitionfinance.presentation.viewmodel.PortfolioScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import java.text.SimpleDateFormat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardCard(
    modifier: Modifier,
    isExpanded: Boolean,
    onCardClicked: () -> Unit,
    viewModel: PortfolioScreenViewModel = viewModel(),
    isin: String,
    ticker: String,
    isCash: Boolean = false,
    product: Product? = null
) {
    val state = viewModel.state.collectAsState()

    val cardHeight = animateDpAsState(
        targetValue = when {
            // Card del Cash
            isCash && isExpanded -> 336.dp
            isCash && !isExpanded -> 160.dp
            // Product card
            !isCash && isExpanded -> 360.dp
            else -> 160.dp
        }
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight.value)
            .clickable(
                onClick = onCardClicked,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.secondary)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                InputCardHeader(
                    label = isin,
                    title = ticker,
                    isExpanded = isExpanded,
                    onCardClicked = onCardClicked,
                    titleFontSize = MaterialTheme.typography.displayLarge.fontSize
                )

                if (isExpanded) {
                    if (isCash) {
                        AmountBox(
                            amount = state.value.cash,
                            onAmountChanged = { newAmount ->
                                newAmount?.let { viewModel.updateCash(it) }
                            },
                            currencySymbol = "€",
                            modifier = modifier.padding(bottom = 36.dp)
                        )

                        when {
                            state.value.usdExchangeState is UiState.Loading ||
                                    state.value.chfExchangeState is UiState.Loading -> {
                                PerformanceBox(
                                    leftAmount = "----",
                                    rightAmount = "----",
                                    leftCurrencySymbol = "$",
                                    rightCurrencySymbol = "₣",
                                    leftLabel = "USD",
                                    rightLabel = "CHF",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            state.value.usdExchangeState is UiState.Error ||
                                    state.value.chfExchangeState is UiState.Error -> {
                                PerformanceBox(
                                    leftAmount = "----",
                                    rightAmount = "----",
                                    leftCurrencySymbol = "$",
                                    rightCurrencySymbol = "₣",
                                    leftLabel = "USD",
                                    rightLabel = "CHF",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            else -> {
                                PerformanceBox(
                                    leftAmount = viewModel.calculateUsdAmount(state.value.cash),
                                    rightAmount = viewModel.calculateChfAmount(state.value.cash),
                                    leftCurrencySymbol = "$",
                                    rightCurrencySymbol = "₣",
                                    leftLabel = "USD",
                                    rightLabel = "CHF",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    } else {
                        if (product != null) {
                            AmountBox(
                                amount = product.amount,
                                onAmountChanged = { newAmount ->
                                    newAmount?.let {
                                        val updatedProduct = product.copy(amount = it)
                                        viewModel.updateProduct(updatedProduct)
                                    }
                                },
                                currencySymbol = "€",
                                modifier = modifier.padding(bottom = 36.dp),
                                isProduct = true
                            )

                            val performance = viewModel.getPerformanceForProduct(product.ticker)

                            if (performance != null) {
                                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                                val formattedPurchaseDate = try {
                                    val date = inputFormat.parse(performance.purchaseDate)
                                    date?.let { outputFormat.format(it) } ?: "Invalid Date"
                                } catch (_: Exception) {
                                    "Invalid Date"
                                }

                                val formattedCurrentDate = try {
                                    val date = inputFormat.parse(performance.currentDate)
                                    date?.let { outputFormat.format(it) } ?: "Invalid Date"
                                } catch (_: Exception) {
                                    "Invalid Date"
                                }

                                PerformanceBox(
                                    leftAmount = performance.purchasePrice.toString(),
                                    rightAmount = performance.currentPrice.toString(),
                                    leftLabel = formattedPurchaseDate,
                                    rightLabel = formattedCurrentDate,
                                    modifier = Modifier.fillMaxWidth(),
                                    onDeleteClicked = { viewModel.removeProduct(product.ticker) }
                                )
                            } else {
                                PerformanceBox(
                                    leftAmount = "----",
                                    rightAmount = "----",
                                    leftLabel = "No data",
                                    rightLabel = "No data",
                                    modifier = Modifier.fillMaxWidth(),
                                    onDeleteClicked = { viewModel.removeProduct(product.ticker) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}