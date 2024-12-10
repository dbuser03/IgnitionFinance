package com.unimib.ignitionfinance.data.remote.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class FirestoreService {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getDocument(collectionPath: String, documentId: String): Map<String, Any>? {
        return try {
            val documentSnapshot = firestore.collection(collectionPath).document(documentId).get().await()
            if (documentSnapshot.exists()) {
                documentSnapshot.data
            } else {
                null
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getCollection(collectionPath: String): QuerySnapshot? {
        return try {
            firestore.collection(collectionPath).get().await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun addDocument(collectionPath: String, data: Map<String, Any>): String? {
        return try {
            val documentReference = firestore.collection(collectionPath).add(data).await()
            documentReference.id
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateDocument(collectionPath: String, documentId: String, data: Map<String, Any>) {
        try {
            firestore.collection(collectionPath).document(documentId).update(data).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun deleteDocument(collectionPath: String, documentId: String) {
        try {
            firestore.collection(collectionPath).document(documentId).delete().await()
        } catch (e: Exception) {
            throw e
        }
    }
}
