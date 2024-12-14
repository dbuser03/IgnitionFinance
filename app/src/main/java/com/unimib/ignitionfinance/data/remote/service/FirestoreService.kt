package com.unimib.ignitionfinance.data.remote.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.unimib.ignitionfinance.data.remote.service.excpetion.FirestoreServiceException
import kotlinx.coroutines.tasks.await

class FirestoreService {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getDocument(collectionPath: String, documentId: String): Map<String, Any>? {
        return try {
            val documentSnapshot = firestore.collection(collectionPath).document(documentId).get().await()
            documentSnapshot.data
        } catch (e: FirebaseFirestoreException) {
            throw FirestoreServiceException("Firestore specific error occurred while getting document", e)
        } catch (e: Exception) {
            throw FirestoreServiceException("General error occurred while getting document", e)
        }
    }

    suspend fun getCollection(collectionPath: String) = try {
        firestore.collection(collectionPath).get().await()
    } catch (e: FirebaseFirestoreException) {
        throw FirestoreServiceException("Firestore specific error occurred while getting collection", e)
    } catch (e: Exception) {
        throw FirestoreServiceException("General error occurred while getting collection", e)
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
        } catch (e: FirebaseFirestoreException) {
            throw FirestoreServiceException("Firestore specific error occurred while adding document", e)
        } catch (e: Exception) {
            throw FirestoreServiceException("General error occurred while adding document", e)
        }
    }

    suspend fun updateDocument(collectionPath: String, documentId: String, data: Map<String, Any>) {
        try {
            firestore.collection(collectionPath).document(documentId).update(data).await()
        } catch (e: FirebaseFirestoreException) {
            throw FirestoreServiceException("Firestore specific error occurred while updating document", e)
        } catch (e: Exception) {
            throw FirestoreServiceException("General error occurred while updating document", e)
        }
    }

    suspend fun deleteDocument(collectionPath: String, documentId: String) {
        try {
            firestore.collection(collectionPath).document(documentId).delete().await()
        } catch (e: FirebaseFirestoreException) {
            throw FirestoreServiceException("Firestore specific error occurred while deleting document", e)
        } catch (e: Exception) {
            throw FirestoreServiceException("General error occurred while deleting document", e)
        }
    }

    suspend fun deleteAllDocuments(collectionPath: String) {
        try {
            val documents = firestore.collection(collectionPath).get().await()
            for (document in documents) {
                document.reference.delete().await()
            }
        } catch (e: FirebaseFirestoreException) {
            throw FirestoreServiceException("Firestore specific error occurred while deleting all documents", e)
        } catch (e: Exception) {
            throw FirestoreServiceException("General error occurred while deleting all documents", e)
        }
    }
}