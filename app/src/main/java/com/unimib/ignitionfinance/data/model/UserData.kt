package com.unimib.ignitionfinance.data.model

data class UserData(
    val name: String,
    val surname: String,
    val authData: AuthData
    // val settingsList: SettingsList
    // val cash: Cash
    // val productList: ProductList
) {
}