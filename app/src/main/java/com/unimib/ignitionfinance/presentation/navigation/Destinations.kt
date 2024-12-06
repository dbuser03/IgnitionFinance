package com.unimib.ignitionfinance.presentation.navigation

sealed class Destinations(val route: String) {
    data object IntroScreen : Destinations("intro_screen")
    data object PortfolioScreen : Destinations("portfolio_screen")
    data object SimulationScreen : Destinations("simulation_screen")
    data object SummaryScreen : Destinations("summary_screen")
    data object SettingsScreen : Destinations("settings_screen")
    data object LoginScreen : Destinations("login_screen")
    data object RegistrationScreen : Destinations("register_screen")
}