package com.unimib.ignitionfinance.auth

import com.google.firebase.auth.FirebaseAuth
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test

class FirebaseAuthRegistrationTest {

    private lateinit var auth: FirebaseAuth

    @Before
    fun setUp() {

        auth = FirebaseAuth.getInstance()
        auth.useEmulator("localhost", 9099)
    }

    @Test
    fun testUserRegistration() {
        val email = "testuser@example.com"
        val password = "securePassword123!"


        val task = auth.createUserWithEmailAndPassword(email, password)
        task.addOnCompleteListener { result ->
            if (result.isSuccessful) {
                val user = auth.currentUser

                assertNotNull(user)
                assertEquals(email, user?.email)
            } else {
                throw Exception("Registrazione fallita: ${result.exception?.message}")
            }
        }
    }
}
