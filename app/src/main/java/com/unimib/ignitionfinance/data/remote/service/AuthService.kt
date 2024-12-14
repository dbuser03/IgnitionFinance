package com.unimib.ignitionfinance.data.remote.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.unimib.ignitionfinance.data.remote.service.excpetion.AuthServiceException
import kotlinx.coroutines.tasks.await

class AuthService {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseUser? {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            authResult.user
        } catch (e: FirebaseAuthException) {
            throw AuthServiceException("Failed to sign in with email and password", e)
        } catch (e: Exception) {
            throw AuthServiceException("An unexpected error occurred", e)
        }
    }

    suspend fun createUserWithEmailAndPassword(email: String, password: String): FirebaseUser? {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            authResult.user
        } catch (e: FirebaseAuthException) {
            throw AuthServiceException("Failed to create user with email and password", e)
        } catch (e: Exception) {
            throw AuthServiceException("An unexpected error occurred", e)
        }
    }

    suspend fun resetPassword(email: String) {
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
        } catch (e: FirebaseAuthException) {
            throw AuthServiceException("Failed to send password reset email", e)
        } catch (e: Exception) {
            throw AuthServiceException("An unexpected error occurred", e)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}