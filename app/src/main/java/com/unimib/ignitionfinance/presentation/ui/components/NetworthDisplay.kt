package com.unimib.ignitionfinance.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.presentation.model.InputBoxModel
import com.unimib.ignitionfinance.presentation.ui.components.settings.input.InputBoxBody
import com.unimib.ignitionfinance.presentation.viewmodel.PortfolioScreenViewModel
//da qui faccio getCash
import com.unimib.ignitionfinance.presentation.viewmodel.SummaryScreenViewModel
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
//scrivo metodo che usi useCase (copia getCash di stef), fare in modo che la quantità sia la somma di getCash+getInvested

@Composable
fun NetworthDisplay(
    inputBoxModel: InputBoxModel,
    portfolioScreenViewModel: PortfolioScreenViewModel,
    summaryScreenViewModel: SummaryScreenViewModel
) {
    val cash = summaryScreenViewModel.cash.collectAsState()
    val cashState = summaryScreenViewModel.cashState.collectAsState()

    LaunchedEffect(Unit) {
        summaryScreenViewModel.getCash()
        summaryScreenViewModel.getInvested()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = inputBoxModel.label,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                /*InputBoxBody(
                    prefix = inputBoxModel.prefix,
                    inputValue = inputBoxModel.inputValue.value.text
                )*/
                when (val state = cashState.value) {
                    is UiState.Loading -> {
                        Text(
                            text = "Calculating...",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    is UiState.Success -> {
                        InputBoxBody(
                            prefix = inputBoxModel.prefix,
                            inputValue = cash.value ?: "0"
                        )
                    }

                    is UiState.Error -> {
                        Text(
                            text = "Error calculating networth",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    else -> Unit
                }
            }
        }
    }
}