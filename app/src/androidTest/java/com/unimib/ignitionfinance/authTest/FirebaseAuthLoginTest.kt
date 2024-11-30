package com.unimib.ignitionfinance.authTest

import com.google.firebase.auth.FirebaseAuth
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test

class FirebaseAuthLoginTest {

    private lateinit var auth: FirebaseAuth

    @Before
    fun setUp() {
        // Configura Firebase Auth per usare l'emulatore
        auth = FirebaseAuth.getInstance()
        auth.useEmulator("localhost", 9099)
    }

    @Test
    fun testUserLogin() {
        val email = "testuser@example.com"
        val password = "securePassword123!"

        // Prima, registriamo l'utente
        val registrationTask = auth.createUserWithEmailAndPassword(email, password)
        registrationTask.addOnCompleteListener { result ->
            if (result.isSuccessful) {
                // Ora eseguiamo il login
                val loginTask = auth.signInWithEmailAndPassword(email, password)
                loginTask.addOnCompleteListener { loginResult ->
                    if (loginResult.isSuccessful) {
                        val user = auth.currentUser
                        // Verifica che l'utente sia autenticato
                        assertNotNull(user)
                        assertEquals(email, user?.email)
                    } else {
                        throw Exception("Login fallito: ${loginResult.exception?.message}")
                    }
                }
            } else {
                throw Exception("Registrazione fallita: ${result.exception?.message}")
            }
        }
    }
}
