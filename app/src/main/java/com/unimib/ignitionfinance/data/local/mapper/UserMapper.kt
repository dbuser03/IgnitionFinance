package com.unimib.ignitionfinance.data.local.mapper

import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.model.UserData
import com.unimib.ignitionfinance.data.model.user.AuthData
import com.unimib.ignitionfinance.data.model.user.Settings
import com.unimib.ignitionfinance.data.model.user.settings.Expenses
import com.unimib.ignitionfinance.data.model.user.settings.Intervals
import com.unimib.ignitionfinance.data.model.user.settings.Withdrawals

object UserMapper {
    fun mapUserToUserData(user: User): UserData {
        return UserData(
            name = user.name,
            surname = user.surname,
            authData = user.authData,
            settings = user.settings,
            cash = user.cash
        )
    }

    fun mapUserDataToUser(userData: UserData?): User {
        return User(
            id = userData?.authData?.id.orEmpty(),
            name = userData?.name.orEmpty(),
            surname = userData?.surname.orEmpty(),
            authData = userData?.authData ?: AuthData(id = "", email = "", displayName = ""),
            settings = userData?.settings ?: Settings(
                withdrawals = Withdrawals("", ""),
                inflationModel = "",
                expenses = Expenses("", "", ""),
                intervals = Intervals("", "", ""),
                numberOfSimulations = ""
            ),
            cash = userData?.cash ?: "0"
        )
    }
}