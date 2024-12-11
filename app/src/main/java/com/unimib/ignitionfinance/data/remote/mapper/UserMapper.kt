package com.unimib.ignitionfinance.data.remote.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.unimib.ignitionfinance.data.model.AuthData
import com.unimib.ignitionfinance.data.model.Expenses
import com.unimib.ignitionfinance.data.model.Intervals
import com.unimib.ignitionfinance.data.model.Settings
import com.unimib.ignitionfinance.data.model.UserData
import com.unimib.ignitionfinance.data.model.Withdrawals

object UserMapper {

    fun mapToUserData(document: DocumentSnapshot): UserData? {
        return try {
            val name = document.getString("name") ?: ""
            val surname = document.getString("surname") ?: ""

            val id = document.getString("authData.id") ?: ""
            val email = document.getString("authData.email") ?: ""
            val displayName = document.getString("authData.displayName") ?: ""

            val withPension = document.getString("settings.withdrawals.withPension") ?: ""
            val withoutPension = document.getString("settings.withdrawals.withoutPension") ?: ""

            val inflationModel = document.getString("settings.inflationModel") ?: ""

            val taxRatePercentage = document.getString("settings.expenses.taxRatePercentage") ?: ""
            val stampDutyPercentage = document.getString("settings.expenses.stampDutyPercentage") ?: ""
            val loadPercentage = document.getString("settings.expenses.loadPercentage") ?: ""

            val yearsInFIRE = document.getString("settings.intervals.yearsInFIRE") ?: ""
            val yearsInPaidRetirement = document.getString("settings.intervals.yearsInPaidRetirement") ?: ""
            val yearsOfBuffer = document.getString("settings.intervals.yearsOfBuffer") ?: ""

            val numberOfSimulations = document.getString("settings.numberOfSimulations") ?: ""

            val authData = AuthData(
                id = id,
                email = email,
                displayName = displayName
            )

            val settings = Settings(
                withdrawals = Withdrawals(
                    withPension = withPension,
                    withoutPension = withoutPension
                ),
                inflationModel = inflationModel,
                expenses = Expenses(
                    taxRatePercentage = taxRatePercentage,
                    stampDutyPercentage = stampDutyPercentage,
                    loadPercentage = loadPercentage
                ),
                intervals = Intervals(
                    yearsInFIRE = yearsInFIRE,
                    yearsInPaidRetirement = yearsInPaidRetirement,
                    yearsOfBuffer = yearsOfBuffer
                ),
                numberOfSimulations = numberOfSimulations
            )

            UserData(
                name = name,
                surname = surname,
                authData = authData,
                settings = settings
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun mapUserToDocument(userData: UserData): Map<String, Any> {
        return mapOf(
            "name" to userData.name,
            "surname" to userData.surname,
            "authData" to mapOf(
                "id" to userData.authData.id,
                "email" to userData.authData.email,
                "displayName" to userData.authData.displayName
            ),
            "settings" to mapOf(
                "withdrawals" to mapOf(
                    "withPension" to userData.settings.withdrawals.withPension,
                    "withoutPension" to userData.settings.withdrawals.withoutPension
                ),
                "inflationModel" to userData.settings.inflationModel,
                "expenses" to mapOf(
                    "taxRatePercentage" to userData.settings.expenses.taxRatePercentage,
                    "stampDutyPercentage" to userData.settings.expenses.stampDutyPercentage,
                    "loadPercentage" to userData.settings.expenses.loadPercentage
                ),
                "intervals" to mapOf(
                    "yearsInFIRE" to userData.settings.intervals.yearsInFIRE,
                    "yearsInPaidRetirement" to userData.settings.intervals.yearsInPaidRetirement,
                    "yearsOfBuffer" to userData.settings.intervals.yearsOfBuffer
                ),
                "numberOfSimulations" to userData.settings.numberOfSimulations
            )
        )
    }
}
