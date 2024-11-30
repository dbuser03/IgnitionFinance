package com.unimib.ignitionfinance.authTest

import com.google.firebase.auth.FirebaseAuth
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test

class FirebaseAuthTest {

    private lateinit var auth: FirebaseAuth

    @Before
    fun setUp() {
        // Configura Firebase Auth per usare l'emulatore
        auth = FirebaseAuth.getInstance()
        auth.useEmulator("localhost", 9099)
    }

    @Test
    fun testUserRegistration() {
        val email = "testuser@example.com"
        val password = "securePassword123"

        // Prova a registrare l'utente
        val task = auth.createUserWithEmailAndPassword(email, password)
        task.addOnCompleteListener { result ->
            if (result.isSuccessful) {
                val user = auth.currentUser
                // Verifica che l'utente sia stato creato correttamente
                assertNotNull(user)
                assertEquals(email, user?.email)
            } else {
                throw Exception("Registrazione fallita: ${result.exception?.message}")
            }
        }
    }
}
