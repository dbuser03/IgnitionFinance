package com.unimib.ignitionfinance.data.remote.service

import com.google.firebase.firestore.FirebaseFirestore
import com.unimib.ignitionfinance.data.remote.service.excpetion.FirestoreServiceException
import kotlinx.coroutines.tasks.await

class FirestoreService {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getDocument(collectionPath: String, documentId: String): Map<String, Any>? {
        return try {
            val documentSnapshot = firestore.collection(collectionPath).document(documentId).get().await()
            documentSnapshot.data
        } catch (e: Exception) {
            throw FirestoreServiceException("Failed to get document", e)
        }
    }

    suspend fun getCollection(collectionPath: String) = try {
        firestore.collection(collectionPath).get().await()
    } catch (e: Exception) {
        throw FirestoreServiceException("Failed to get collection", e)
    }

    suspend fun addDocument(
        collectionPath: String,
        data: Map<String, Any>,
        documentId: String? = null
    ): String? {
        return try {
            val collectionReference = firestore.collection(collectionPath)
            val documentReference = if (documentId != null) {
                collectionReference.document(documentId).set(data).await()
                collectionReference.document(documentId)
            } else {
                collectionReference.add(data).await()
            }
            documentReference.id
        } catch (e: Exception) {
            throw FirestoreServiceException("Failed to add document", e)
        }
    }

    suspend fun updateDocument(collectionPath: String, documentId: String, data: Map<String, Any>) {
        try {
            firestore.collection(collectionPath).document(documentId).update(data).await()
        } catch (e: Exception) {
            throw FirestoreServiceException("Failed to update document", e)
        }
    }

    suspend fun deleteDocument(collectionPath: String, documentId: String) {
        try {
            firestore.collection(collectionPath).document(documentId).delete().await()
        } catch (e: Exception) {
            throw FirestoreServiceException("Failed to delete document", e)
        }
    }

    suspend fun deleteAllDocuments(collectionPath: String) {
        try {
            val documents = firestore.collection(collectionPath).get().await()
            for (document in documents) {
                document.reference.delete().await()
            }
        } catch (e: Exception) {
            throw FirestoreServiceException("Failed to delete all documents", e)
        }
    }
}