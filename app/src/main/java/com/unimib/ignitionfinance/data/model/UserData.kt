package com.unimib.ignitionfinance.data.model

import com.unimib.ignitionfinance.data.model.user.AuthData
import com.unimib.ignitionfinance.data.model.user.Settings

data class UserData(
    val name: String,
    val surname: String,
    val authData: AuthData,
    val settings: Settings
    // val cash: Cash
    // val productList: ProductList
) {
}