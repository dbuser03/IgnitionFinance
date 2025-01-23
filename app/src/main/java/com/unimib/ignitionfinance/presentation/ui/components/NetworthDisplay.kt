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
import java.text.NumberFormat
import java.util.Locale

//scrivo metodo che usi useCase (copia getCash di stef), fare in modo che la quantità sia la somma di getCash+getInvested

@Composable
fun NetworthDisplay(
    inputBoxModel: InputBoxModel,
    portfolioScreenViewModel: PortfolioScreenViewModel,
    summaryScreenViewModel: SummaryScreenViewModel
) {
    //val cash = portfolioScreenViewModel.cash.collectAsState()
    val cashState = portfolioScreenViewModel.cashState.collectAsState()
    //val invested = summaryScreenViewModel.invested.collectAsState()
    val investedState = summaryScreenViewModel.investedState.collectAsState()

    LaunchedEffect(Unit) {
        portfolioScreenViewModel.getCash()
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

                when {
                    cashState.value is UiState.Loading || investedState.value is UiState.Loading -> {
                        Text(
                            text = "Calculating...",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    cashState.value is UiState.Success && investedState.value is UiState.Success -> {
                        val cashString = (cashState.value as UiState.Success<String>).data
                        val cleanCashString = cashString.replace("[^0-9.]".toRegex(),"")
                        val cash = cleanCashString.toDoubleOrNull() ?: 0.0
                        val invested = (investedState.value as UiState.Success<Double>).data
                        val networth = cash + invested
                        val formattedNetworth = NumberFormat.getCurrencyInstance(Locale.US).format(networth)

                        InputBoxBody(
                            prefix = inputBoxModel.prefix,
                            inputValue = formattedNetworth
                        )
                    }

                    else -> {
                        Text(
                            text = "Error calculating networth",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    //else -> Unit
                }
            }
        }
    }
}