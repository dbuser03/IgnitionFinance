package com.unimib.ignitionfinance.presentation.ui.components.settings.select

data class SelectBoxModel(
    val text: String,
    val displayedTexts: List<String>,
    val selectedText: String? = null
) {
    fun selectText(newText: String): SelectBoxModel {
        return this.copy(selectedText = newText)
    }
}
