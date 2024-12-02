package com.unimib.ignitionfinance.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme
import androidx.compose.ui.text.input.TextFieldValue
import com.unimib.ignitionfinance.R

@Composable
fun ExpandableCard(
    label: String,
    title: String,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false,
    inputValues: List<MutableState<TextFieldValue>>,
    prefixes: List<String> = listOf("€"),
    iconResIds: List<Int> = listOf(R.drawable.outline_person_4_24),
    inputBoxes: List<String>
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }

    val cardInputBoxHeight = 64.dp
    val spacerHeight = 24.dp

    val cardHeight by animateDpAsState(
        targetValue = if (isExpanded) {
            val totalInputBoxHeight = cardInputBoxHeight * inputBoxes.size
            val totalSpacerHeight = spacerHeight * (inputBoxes.size)
            totalInputBoxHeight + totalSpacerHeight + 104.dp
        } else {
            104.dp
        },
        label = ""
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clickable { isExpanded = !isExpanded },
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    inputBoxes.forEachIndexed { index, label ->
                        val prefix = prefixes.getOrElse(index) { "€" }
                        val iconResId = iconResIds.getOrElse(index) { R.drawable.outline_person_4_24 }
                        val inputValue = inputValues.getOrElse(index) { mutableStateOf(TextFieldValue("")) }  // Get the specific input value for each input box

                        CardInputBox(
                            text = label,
                            prefix = prefix,
                            inputValue = inputValue,
                            iconResId = iconResId
                        )
                        if (index < inputBoxes.size - 1) {
                            Spacer(modifier = Modifier.height(spacerHeight))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ExpandableCardWithdrawalPreview() {
    IgnitionFinanceTheme {
        val inputValues = List(2) { remember { mutableStateOf(TextFieldValue("----")) } }  // List of mutable states for input values
        ExpandableCard(
            label = "NORMAL, RETIREMENT",
            title = "WITHDRAW",
            initiallyExpanded = true,
            inputValues = inputValues,
            prefixes = listOf("€", "€"),
            iconResIds = listOf(R.drawable.outline_person_apron_24, R.drawable.outline_person_4_24),
            inputBoxes = listOf("Monthly withdrawals (no pension)", "Monthly withdrawals (with pension)")
        )
    }
}

@Preview
@Composable
fun ExpandableCardExpensesPreview() {
    IgnitionFinanceTheme {
        val inputValues = List(3) { remember { mutableStateOf(TextFieldValue("----")) } }  // List of mutable states for input values
        ExpandableCard(
            label = "TAX RATE, STAMP DUTY, LOAD",
            title = "EXPENSES",
            initiallyExpanded = true,
            inputValues = inputValues,
            prefixes = listOf("%", "%", "%"),
            iconResIds = listOf(R.drawable.outline_account_balance_24, R.drawable.outline_position_top_right_24, R.drawable.outline_weight_24),
            inputBoxes = listOf("Tax Rate Percentage", "Stamp Duty Percentage", "Load Percentage")
        )
    }
}

@Preview
@Composable
fun ExpandableCardIntervalPreview() {
    IgnitionFinanceTheme {
        val inputValues = List(3) { remember { mutableStateOf(TextFieldValue("----")) } }
        ExpandableCard(
            label = "YEARS, RETIREMENTS YEARS, BUFFER",
            title = "INTERVALS",
            initiallyExpanded = true,
            inputValues = inputValues,
            prefixes = listOf("YRS", "YRS", "YRS"),
            iconResIds = listOf(R.drawable.outline_local_fire_department_24, R.drawable.outline_send_money_24, R.drawable.outline_clock_loader_10_24),
            inputBoxes = listOf("Years in FIRE", "Years in paid retirement", "Years of buffer")
        )
    }
}

@Preview
@Composable
fun ExpandableCardSimulationsPreview() {
    IgnitionFinanceTheme {
        val inputValues = List(3) { remember { mutableStateOf(TextFieldValue("----")) } }
        ExpandableCard(
            label = "NUMBER",
            title = "SIMULATIONS",
            initiallyExpanded = true,
            inputValues = inputValues,
            prefixes = listOf("N°"),
            iconResIds = listOf(R.drawable.outline_autoplay_24),
            inputBoxes = listOf("Number of simulations to perform")
        )
    }
}
