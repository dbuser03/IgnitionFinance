package com.unimib.ignitionfinance.domain.validation

object RegistrationValidationRules {
    fun validateName(name: String): Boolean {
        val isValidLength = name.length >= 3
        val containsNumbers = name.any { it.isDigit() }
        val isSingleWord = name.trim().split("\\s+".toRegex()).size == 1
        val noWhiteSpaces = !name.contains(" ")
        val noPunctuation = !name.any { it in ".,;!?()[]{}<>:/" }
        return isValidLength && !containsNumbers && isSingleWord && noWhiteSpaces && noPunctuation
    }

    fun validateSurname(surname: String): Boolean {
        val isValidLength = surname.length >= 3
        val containsNumbers = surname.any { it.isDigit() }
        val isSingleWord = surname.trim().split("\\s+".toRegex()).size == 1
        val noWhiteSpaces = !surname.contains(" ")
        val noPunctuation = !surname.any { it in ".,;!?()[]{}<>:/" }
        return isValidLength && !containsNumbers && isSingleWord && noWhiteSpaces && noPunctuation
    }
}
