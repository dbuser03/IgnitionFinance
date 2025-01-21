@file:Suppress("UNCHECKED_CAST")

package com.unimib.ignitionfinance.data.remote.mapper

import com.unimib.ignitionfinance.data.model.user.AuthData
import com.unimib.ignitionfinance.data.model.user.settings.Expenses
import com.unimib.ignitionfinance.data.model.user.settings.Intervals
import com.unimib.ignitionfinance.data.model.user.Settings
import com.unimib.ignitionfinance.data.model.UserData
import com.unimib.ignitionfinance.data.model.user.DailyReturn
import com.unimib.ignitionfinance.data.model.user.Product
import com.unimib.ignitionfinance.data.model.user.settings.Withdrawals
import java.math.BigDecimal

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
                cash = (this["cash"] as? String) ?: "0",
                productList = mapProductList(this["productList"] as? List<Map<String, Any>>),
                firstAdded = (this["firstAdded"] as? Boolean) ?: false,
                dataset = mapDataset(this["dataset"] as? List<Map<DailyReturn, Any>>),
            )
        }
    }
    private fun mapDataset(dataset: List<Any>?): List<DailyReturn> {
        return dataset?.map { pair ->
            val map = pair as? Map<String, Any>
            val date = (map?.get("first") as? String).orEmpty()
            val value = (map?.get("second") as? String)?.toBigDecimalOrNull() ?: BigDecimal.ZERO
            DailyReturn(date, value)
        } ?: emptyList()
    }

    private fun mapProductList(products: List<Map<String, Any>>?): List<Product> {
        return products?.map { productMap ->
            Product(
                isin = (productMap["isin"] as? String).orEmpty(),
                ticker = (productMap["ticker"] as? String).orEmpty(),
                purchaseDate = (productMap["purchaseDate"] as? String).orEmpty(),
                amount = (productMap["amount"] as? String).orEmpty(),
            )
        } ?: emptyList()
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
            "updatedAt" to userData.updatedAt,
            "cash" to userData.cash,
            "productList" to userData.productList.map { product ->
                mapOf(
                    "isin" to product.isin,
                    "ticker" to product.ticker,
                    "amount" to product.amount,
                    "purchaseDate" to product.purchaseDate,
                )
            },
            "firstAdded" to userData.firstAdded,
            "dataset" to userData.dataset.map { dailyReturn ->
                mapOf(
                    "dates" to dailyReturn.dates,
                    "weightedReturns" to dailyReturn.weightedReturns.toPlainString()
                )
            }
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
