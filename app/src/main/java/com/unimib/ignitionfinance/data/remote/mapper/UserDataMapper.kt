@file:Suppress("UNCHECKED_CAST")

package com.unimib.ignitionfinance.data.remote.mapper

import com.unimib.ignitionfinance.data.model.user.AuthData
import com.unimib.ignitionfinance.data.model.user.settings.Expenses
import com.unimib.ignitionfinance.data.model.user.settings.Intervals
import com.unimib.ignitionfinance.data.model.user.Settings
import com.unimib.ignitionfinance.data.model.UserData
import com.unimib.ignitionfinance.data.model.user.settings.Withdrawals

object UserDataMapper {

    fun mapDocumentToUserData(document: Map<String, Any>?): UserData? {
        return document?.run {
            UserData(
                name = (this["name"] as? String).orEmpty(),
                surname = (this["surname"] as? String).orEmpty(),
                authData = mapAuthData(this["authData"] as? Map<String, Any>),
                settings = mapSettings(this["settings"] as? Map<String, Any>),
                createdAt = (this["createdAt"] as? Long) ?: System.currentTimeMillis(),
                updatedAt = (this["updatedAt"] as? Long) ?: System.currentTimeMillis()
            )
        }
    }

    private fun mapAuthData(authData: Map<String, Any>?): AuthData {
        return AuthData(
            id = (authData?.get("id") as? String).orEmpty(),
            email = (authData?.get("email") as? String).orEmpty(),
            displayName = (authData?.get("displayName") as? String).orEmpty()
        )
    }

    private fun mapSettings(settings: Map<String, Any>?): Settings {
        return Settings(
            withdrawals = Withdrawals(
                withPension = (settings?.get("withdrawals.withPension") as? String).orEmpty(),
                withoutPension = (settings?.get("withdrawals.withoutPension") as? String).orEmpty()
            ),
            inflationModel = (settings?.get("inflationModel") as? String).orEmpty(),
            expenses = Expenses(
                taxRatePercentage = (settings?.get("expenses.taxRatePercentage") as? String).orEmpty(),
                stampDutyPercentage = (settings?.get("expenses.stampDutyPercentage") as? String).orEmpty(),
                loadPercentage = (settings?.get("expenses.loadPercentage") as? String).orEmpty()
            ),
            intervals = Intervals(
                yearsInFIRE = (settings?.get("intervals.yearsInFIRE") as? String).orEmpty(),
                yearsInPaidRetirement = (settings?.get("intervals.yearsInPaidRetirement") as? String).orEmpty(),
                yearsOfBuffer = (settings?.get("intervals.yearsOfBuffer") as? String).orEmpty()
            ),
            numberOfSimulations = (settings?.get("numberOfSimulations") as? String).orEmpty()
        )
    }


    fun mapUserDataToDocument(userData: UserData): Map<String, Any> {
        return mapOf(
            "name" to userData.name,
            "surname" to userData.surname,
            "authData" to mapOf(
                "id" to userData.authData.id,
                "email" to userData.authData.email,
                "displayName" to userData.authData.displayName
            ),
            "settings" to mapSettingsToMap(userData.settings)
        )
    }

    private fun mapSettingsToMap(settings: Settings): Map<String, Any> {
        return mapOf(
            "withdrawals" to mapOf(
                "withPension" to settings.withdrawals.withPension,
                "withoutPension" to settings.withdrawals.withoutPension
            ),
            "inflationModel" to settings.inflationModel,
            "expenses" to mapOf(
                "taxRatePercentage" to settings.expenses.taxRatePercentage,
                "stampDutyPercentage" to settings.expenses.stampDutyPercentage,
                "loadPercentage" to settings.expenses.loadPercentage
            ),
            "intervals" to mapOf(
                "yearsInFIRE" to settings.intervals.yearsInFIRE,
                "yearsInPaidRetirement" to settings.intervals.yearsInPaidRetirement,
                "yearsOfBuffer" to settings.intervals.yearsOfBuffer
            ),
            "numberOfSimulations" to settings.numberOfSimulations
        )
    }
}