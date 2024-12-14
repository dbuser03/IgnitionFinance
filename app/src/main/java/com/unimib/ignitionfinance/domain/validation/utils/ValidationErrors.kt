package com.unimib.ignitionfinance.domain.validation.utils

object ValidationErrors {
    object Common {
        const val INVALID_EMAIL = "Email should be valid and follow the standard format (e.g., user@example.com)"
        const val INVALID_PASSWORD = "Password must be at least 8 characters long, include at least one uppercase letter, one lowercase letter, one number, and one special character (e.g., @, !, ?, #, \$, etc.)."
    }

    object Input {
        const val POSITIVE_EURO = "Input should be greater than 0 €"
        const val PERCENTAGE_RANGE = "Input should be between 0 and 100 %"
        const val YEARS_LIMIT = "Input should be < 100 YRS"
        const val NUMBER_RANGE = "Input should be between 1 and 10000 N°"
        const val INVALID_INPUT = "Invalid input for prefix %s"
    }

    object Login {
        const val INVALID_FORM = "Invalid login form"
        const val EMAIL_ERROR = Common.INVALID_EMAIL
        const val PASSWORD_ERROR = Common.INVALID_PASSWORD
    }

    object Registration {
        const val INVALID_NAME = "Name should be at least 2 characters and not contain numbers"
        const val INVALID_SURNAME = "Surname should be at least 2 characters and not contain numbers"
        const val EMAIL_ERROR = Common.INVALID_EMAIL
        const val PASSWORD_ERROR = Common.INVALID_PASSWORD
        const val INVALID_FORM = "Invalid registration form"
    }

    object Reset {
        const val EMAIL_ERROR = Common.INVALID_EMAIL
        const val INVALID_FORM = "Invalid password reset form"
    }
}
