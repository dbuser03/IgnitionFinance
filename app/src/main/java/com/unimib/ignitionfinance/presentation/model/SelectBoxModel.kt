package com.unimib.ignitionfinance.presentation.model

data class SelectBoxModel(
    val text: String,
    val displayedTexts: List<String>,
    val selectedText: String? = null
)
