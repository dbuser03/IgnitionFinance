package com.unimib.ignitionfinance.presentation.ui.components.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.presentation.ui.components.CustomIcon
import com.unimib.ignitionfinance.presentation.ui.components.dialog.DialogManager
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import com.unimib.ignitionfinance.R

@Composable
fun ProductBox(
    amount: String,
    onAmountChanged: ((String?) -> Unit)? = null,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    alignRight: Boolean = false,
    bottomLabel: String? = null,
    isReadOnly: Boolean = false
) {
    var showDialog by remember { mutableStateOf(false) }

    if (!isReadOnly && onAmountChanged != null) {
        DialogManager(
            showDialog = showDialog,
            onDismissRequest = { showDialog = false },
            onConfirmation = { newValue ->
                showDialog = false
                onAmountChanged(newValue)
            },
            dialogTitle = "Update product amount",
            prefix = currencySymbol
        )
    }

    val formattedAmount = remember(amount) {
        formatNumberAmerican(amount)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = !isReadOnly,
                    onClick = { if (!isReadOnly) showDialog = true },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (alignRight) Arrangement.End else Arrangement.Start
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (alignRight) Arrangement.End else Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currencySymbol,
                            color = if (isReadOnly) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.secondary,
                            style = if (isReadOnly) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.width(if (isReadOnly) 4.dp else 8.dp))
                        Text(
                            text = formattedAmount,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = if (isReadOnly) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium
                        )
                    }
                }

                if (!isReadOnly) {
                    CustomIcon(
                        icon = painterResource(id = R.drawable.outline_monetization_on_24),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }

        bottomLabel?.let { label ->
            Text(
                text = label,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = if (alignRight) TextAlign.End else TextAlign.Start
            )
        }
    }
}

private fun formatNumberAmerican(input: String): String {
    return try {
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            groupingSeparator = ','
            decimalSeparator = '.'
        }
        val formatter = DecimalFormat("#,###.##", symbols).apply {
            isGroupingUsed = true
            maximumFractionDigits = 2
        }

        input.toDoubleOrNull()?.let { formatter.format(it) } ?: input
    } catch (_: NumberFormatException) {
        input
    }
}
