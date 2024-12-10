package com.unimib.ignitionfinance.data.remote.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.unimib.ignitionfinance.data.model.AuthData
import com.unimib.ignitionfinance.data.model.UserData

object UserMapper {

    fun mapToUserData(document: DocumentSnapshot): UserData? {
        return try {
            val name = document.getString("name") ?: ""
            val surname = document.getString("surname") ?: ""

            val id = document.getString("authData.id") ?: ""
            val email = document.getString("authData.email") ?: ""
            val displayName = document.getString("authData.displayName") ?: ""

            val authData = AuthData(
                id = id,
                email = email,
                displayName = displayName
            )

            UserData(
                name = name,
                surname = surname,
                authData = authData
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
            )
        )
    }
}