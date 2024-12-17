package com.unimib.ignitionfinance.data.local.mapper

import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.model.UserData

object UserMapper {

    fun mapUserToUserData(user: User): UserData {
        return UserData(
            name = user.name,
            surname = user.surname,
            authData = user.authData,
            settings = user.settings
        )
    }
}