@file:Suppress("UNREACHABLE_CODE")

package com.unimib.ignitionfinance.presentation.ui.components

import android.annotation.SuppressLint
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.presentation.model.InputBoxModel
import com.unimib.ignitionfinance.presentation.ui.components.settings.input.InputBoxBody
//import com.unimib.ignitionfinance.presentation.viewmodel.PortfolioScreenViewModel
//import com.unimib.ignitionfinance.presentation.viewmodel.SettingsScreenViewModel

@Composable
fun NetworthDisplay(
    inputBoxModel: InputBoxModel,
    //portfolioViewModel: PortfolioScreenViewModel,
    //summaryScreenViewModel: SummaryScreenViewModel
) {
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
                InputBoxBody(
                    prefix = inputBoxModel.prefix,
                    inputValue = inputBoxModel.inputValue.value.text
                )
            }
        }
    }
}