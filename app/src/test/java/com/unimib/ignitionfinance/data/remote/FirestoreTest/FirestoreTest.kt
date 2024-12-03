package com.unimib.ignitionfinance.data.remote.FirestoreTest

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.junit.Before
import org.junit.Test

class FirestoreTest {

    private lateinit var firestore: FirebaseFirestore

    @Before
    fun setup() {
        FirebaseApp.initializeApp() // Inizializza Firebase (se non l'hai già fatto)
        firestore = Firebase.firestore

        // Configura Firestore per utilizzare l'emulatore
        firestore.useEmulator("10.0.2.2", 8080) // Usa "10.0.2.2" per l'emulatore Android
    }

    @Test
    fun testFirestoreWriteAndRead() {
        // Scrivi un documento nel database
        val testData = hashMapOf(
            "name" to "Test User",
            "email" to "testuser@example.com"
        )

        firestore.collection("users").document("testUser")
            .set(testData)
            .addOnSuccessListener {
                println("Documento scritto con successo!")
            }
            .addOnFailureListener { e ->
                println("Errore nella scrittura del documento: $e")
            }

        // Leggi il documento
        firestore.collection("users").document("testUser")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    println("Documento trovato: ${document.data}")
                } else {
                    println("Nessun documento trovato!")
                }
            }
            .addOnFailureListener { e ->
                println("Errore nella lettura del documento: $e")
            }
    }
}
