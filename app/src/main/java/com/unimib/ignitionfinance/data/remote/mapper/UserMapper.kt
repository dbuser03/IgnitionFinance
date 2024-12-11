package com.unimib.ignitionfinance.data.remote.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.unimib.ignitionfinance.data.model.AuthData
import com.unimib.ignitionfinance.data.model.Expenses
import com.unimib.ignitionfinance.data.model.Intervals
import com.unimib.ignitionfinance.data.model.Settings
import com.unimib.ignitionfinance.data.model.UserData
import com.unimib.ignitionfinance.data.model.Withdrawals
import com.unimib.ignitionfinance.data.local.entity.User

object UserMapper {

    fun mapToUserData(document: DocumentSnapshot?): UserData? {
        return try {
            if (document == null) return null

            val name = document.getString("name") ?: ""
            val surname = document.getString("surname") ?: ""

            val authData = document.get("authData") as? Map<*, *>
            val id = authData?.get("id") as? String ?: ""
            val email = authData?.get("email") as? String ?: ""
            val displayName = authData?.get("displayName") as? String ?: ""

            val withdrawals = document.get("settings.withdrawals") as? Map<*, *>
            val withPension = withdrawals?.get("withPension") as? String ?: ""
            val withoutPension = withdrawals?.get("withoutPension") as? String ?: ""

            val inflationModel = document.getString("settings.inflationModel") ?: ""

            val expenses = document.get("settings.expenses") as? Map<*, *>
            val taxRatePercentage = expenses?.get("taxRatePercentage") as? String ?: ""
            val stampDutyPercentage = expenses?.get("stampDutyPercentage") as? String ?: ""
            val loadPercentage = expenses?.get("loadPercentage") as? String ?: ""

            val intervals = document.get("settings.intervals") as? Map<*, *>
            val yearsInFIRE = intervals?.get("yearsInFIRE") as? String ?: ""
            val yearsInPaidRetirement = intervals?.get("yearsInPaidRetirement") as? String ?: ""
            val yearsOfBuffer = intervals?.get("yearsOfBuffer") as? String ?: ""

            val numberOfSimulations = document.getString("settings.numberOfSimulations") ?: ""

            val authDataObj = AuthData(
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
                authData = authDataObj,
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

    fun mapUserDataToUser(userData: UserData): User {
        return User(
            id = userData.authData.id,
            name = userData.name,
            surname = userData.surname,
            authData = userData.authData,
            settings = userData.settings
        )
    }

    fun mapUserToUserData(user: User): UserData {
        return UserData(
            name = user.name,
            surname = user.surname,
            authData = user.authData,
            settings = user.settings
        )
    }
}
