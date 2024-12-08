package com.unimib.ignitionfinance.data.remote.mapper

import com.google.firebase.auth.FirebaseUser
import com.unimib.ignitionfinance.data.model.AuthData

object AuthMapper {
    fun mapToUser(firebaseUser: FirebaseUser): AuthData {
        return AuthData(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            displayName = firebaseUser.displayName ?: ""
        )
    }
}