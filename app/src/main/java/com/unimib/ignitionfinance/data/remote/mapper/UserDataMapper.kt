package com.unimib.ignitionfinance.data.remote.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.unimib.ignitionfinance.data.model.user.AuthData
import com.unimib.ignitionfinance.data.model.user.settings.Expenses
import com.unimib.ignitionfinance.data.model.user.settings.Intervals
import com.unimib.ignitionfinance.data.model.user.Settings
import com.unimib.ignitionfinance.data.model.UserData
import com.unimib.ignitionfinance.data.model.user.settings.Withdrawals

object UserDataMapper {

    fun mapDocumentToUserData(document: DocumentSnapshot?): UserData? {
        return document?.run {
            UserData(
                name = getString("name").orEmpty(),
                surname = getString("surname").orEmpty(),
                authData = mapAuthData(get("authData") as? Map<*, *>),
                settings = mapSettings(get("settings") as? Map<*, *>)
            )
        }
    }

    private fun mapAuthData(authData: Map<*, *>?): AuthData {
        return AuthData(
            id = authData?.get("id") as? String ?: "",
            email = authData?.get("email") as? String ?: "",
            displayName = authData?.get("displayName") as? String ?: ""
        )
    }

    private fun mapSettings(settings: Map<*, *>?): Settings {
        return Settings(
            withdrawals = Withdrawals(
                withPension = settings?.get("withdrawals.withPension") as? String ?: "",
                withoutPension = settings?.get("withdrawals.withoutPension") as? String ?: ""
            ),
            inflationModel = settings?.get("inflationModel") as? String ?: "",
            expenses = Expenses(
                taxRatePercentage = settings?.get("expenses.taxRatePercentage") as? String ?: "",
                stampDutyPercentage = settings?.get("expenses.stampDutyPercentage") as? String ?: "",
                loadPercentage = settings?.get("expenses.loadPercentage") as? String ?: ""
            ),
            intervals = Intervals(
                yearsInFIRE = settings?.get("intervals.yearsInFIRE") as? String ?: "",
                yearsInPaidRetirement = settings?.get("intervals.yearsInPaidRetirement") as? String ?: "",
                yearsOfBuffer = settings?.get("intervals.yearsOfBuffer") as? String ?: ""
            ),
            numberOfSimulations = settings?.get("numberOfSimulations") as? String ?: ""
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