package com.unimib.ignitionfinance.presentation.ui.components.navigation

data class BottomNavigationItemModel(
    val iconRes: Int,
    val label: String,
    val contentDescription: String? = null,
    val destination: String
)
