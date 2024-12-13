package com.unimib.ignitionfinance.firestore

import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test

class FirestoreUnitTest {

    private lateinit var firestore: FirebaseFirestore

    @Before
    fun setup() {
        // Configura Firestore per connettersi all'emulatore
        firestore = FirebaseFirestore.getInstance()
        firestore.useEmulator("127.0.0.1", 8080) // Usa localhost per i test locali
    }

    @Test
    fun testFirestoreWriteAndRead() {
        val testData = hashMapOf(
            "name" to "Test User",
            "email" to "testuser@example.com"
        )

        // Scrivi e leggi un documento
        firestore.collection("users").document("testUser").set(testData)
            .addOnSuccessListener {
                println("Documento scritto correttamente!")
            }
            .addOnFailureListener { e ->
                println("Errore: ${e.message}")
            }

        firestore.collection("users").document("testUser").get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    println("Documento trovato: ${document.data}")
                } else {
                    println("Documento non trovato!")
                }
            }
            .addOnFailureListener { e ->
                println("Errore nella lettura: ${e.message}")
            }
    }
}
