package com.unimib.ignitionfinance.presentation.ui.components.portfolio

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unimib.ignitionfinance.presentation.ui.components.settings.input.InputCardHeader
import com.unimib.ignitionfinance.presentation.viewmodel.PortfolioScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState

@Composable
fun CashCard(
    modifier: Modifier,
    isExpanded: Boolean,
    onCardClicked: () -> Unit,
    viewModel: PortfolioScreenViewModel = viewModel()
) {
    val label = "BANK ACCOUNT"
    val title = "CASH"
    val state = viewModel.state.collectAsState()

    val cardHeight = animateDpAsState(
        targetValue = if (isExpanded) {
            336.dp
        } else {
            160.dp
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
                .background(MaterialTheme.colorScheme.secondary),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputCardHeader(
                label = label,
                title = title,
                isExpanded = isExpanded,
                onCardClicked = onCardClicked,
                titleFontSize = MaterialTheme.typography.displayLarge.fontSize
            )

            if (isExpanded) {
                CashBox(
                    amount = state.value.cash,
                    onAmountChanged = { newAmount ->
                        newAmount?.let { viewModel.updateCash(it) }
                    },
                    currencySymbol = "€",
                    modifier = modifier.padding(bottom = 48.dp)
                )

                when {
                    state.value.usdExchangeState is UiState.Loading ||
                            state.value.chfExchangeState is UiState.Loading -> {
                        CashPerformance(
                            usdAmount = "----",
                            chfAmount = "----",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    state.value.usdExchangeState is UiState.Error ||
                            state.value.chfExchangeState is UiState.Error -> {
                        CashPerformance(
                            usdAmount = "----",
                            chfAmount = "----",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    else -> {
                        CashPerformance(
                            usdAmount = viewModel.calculateUsdAmount(state.value.cash),
                            chfAmount = viewModel.calculateChfAmount(state.value.cash),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}