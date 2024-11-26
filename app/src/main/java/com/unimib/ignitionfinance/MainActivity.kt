package com.unimib.ignitionfinance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.unimib.ignitionfinance.ui.screens.IntroScreen
import com.unimib.ignitionfinance.ui.screens.PortfolioScreen
import com.unimib.ignitionfinance.ui.theme.IgnitionFinanceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IgnitionFinanceTheme {
                IntroScreen()
                //PortfolioScreen()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IgnitionFinanceTheme {
        IntroScreen()
        //PortfolioScreen()
    }
}