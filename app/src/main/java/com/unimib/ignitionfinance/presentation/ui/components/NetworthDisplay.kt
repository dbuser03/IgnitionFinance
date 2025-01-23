package com.unimib.ignitionfinance.presentation.ui.components

import android.util.Log
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
    val cash = portfolioScreenViewModel.cash.collectAsState()
    val cashState = portfolioScreenViewModel.cashState.collectAsState()
    val invested = summaryScreenViewModel.invested.collectAsState()
    val investedState = summaryScreenViewModel.investedState.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("NetworthDisplay", "Launching getCash and getInvested")
        portfolioScreenViewModel.getCash()
        summaryScreenViewModel.getInvested()
    }

    /*LaunchedEffect(cash.value, invested.value) {
        Log.d("NetworthDisplay", "Cash value: ${cash.value}")
        Log.d("NetworthDisplay", "Invested value: ${invested.value}")

        val cashValue = cash.value?.toDoubleOrNull() ?: 0.0
        val investedValue = invested.value?.toDoubleOrNull() ?: 0.0
        val totalNetworth = cashValue + investedValue

        Log.d("NetworthDisplay", "Total Networth: $totalNetworth")
    }*/

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
                        val cashValue = cash.value ?: "0"
                        val investedValue = invested.value ?: "0"

                        Log.d("NetworthDisplay", "Displaying - Cash: $cashValue, Invested: $investedValue")

                        val totalNetworth = try {
                            (cashValue.toDoubleOrNull() ?: 0.0) + (investedValue.toDoubleOrNull() ?: 0.0)
                        } catch (e: Exception) {
                            Log.e("NetworthDisplay", "Error calculating networth", e)
                            0.0
                        }

                        InputBoxBody(
                            prefix = inputBoxModel.prefix,
                            //inputValue = cash.value ?: "0"
                            //inputValue = String.format("%.2f", totalNetworth)
                            inputValue = totalNetworth.toString()
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