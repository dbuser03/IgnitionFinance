package com.unimib.ignitionfinance.ui.navigation

sealed class Destinations(val route: String) {
    object IntroScreen : Destinations("intro_screen")
    object PortfolioScreen : Destinations("portfolio_screen")
    object SimulationScreen : Destinations("simulation_screen")
    object SummaryScreen : Destinations("summary_screen")
    object SettingsScreen : Destinations("settings_screen")

}