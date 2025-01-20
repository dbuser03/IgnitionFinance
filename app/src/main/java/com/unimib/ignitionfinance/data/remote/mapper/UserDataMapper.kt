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
                updatedAt = (this["updatedAt"] as? Long) ?: System.currentTimeMillis(),
                cash = (this["cash"] as? String) ?: "0"
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
                withPension = (settings?.get("withdrawals") as? Map<String, Any>)?.get("withPension") as? String ?: "",
                withoutPension = (settings?.get("withdrawals") as? Map<String, Any>)?.get("withoutPension") as? String ?: ""
            ),
            inflationModel = (settings?.get("inflationModel") as? String).orEmpty(),
            expenses = Expenses(
                taxRatePercentage = (settings?.get("expenses") as? Map<String, Any>)?.get("taxRatePercentage") as? String ?: "",
                stampDutyPercentage = (settings?.get("expenses") as? Map<String, Any>)?.get("stampDutyPercentage") as? String ?: "",
                loadPercentage = (settings?.get("expenses") as? Map<String, Any>)?.get("loadPercentage") as? String ?: ""
            ),
            intervals = Intervals(
                yearsInFIRE = (settings?.get("intervals") as? Map<String, Any>)?.get("yearsInFIRE") as? String ?: "",
                yearsInPaidRetirement = (settings?.get("intervals") as? Map<String, Any>)?.get("yearsInPaidRetirement") as? String ?: "",
                yearsOfBuffer = (settings?.get("intervals") as? Map<String, Any>)?.get("yearsOfBuffer") as? String ?: ""
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
            "settings" to mapSettingsToMap(userData.settings),
            "createdAt" to userData.createdAt,
            "updatedAt" to userData.updatedAt
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
