package com.unimib.ignitionfinance.data.repository

import com.google.firebase.firestore.QuerySnapshot
import com.unimib.ignitionfinance.data.remote.service.FirestoreService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface FirestoreRepository {
    suspend fun getDocumentById(collectionPath: String, documentId: String): Flow<Result<Map<String, Any>?>>
    suspend fun getAllDocuments(collectionPath: String): Flow<Result<QuerySnapshot?>>
    suspend fun addDocument(collectionPath: String, data: Map<String, Any>, documentId: String? = null): Flow<Result<String?>>
    suspend fun updateDocument(collectionPath: String, documentId: String, data: Map<String, Any>): Flow<Result<Unit>>
    suspend fun deleteDocument(collectionPath: String, documentId: String): Flow<Result<Unit>>
}

class FirestoreRepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreService
) : FirestoreRepository {

    override suspend fun getDocumentById(collectionPath: String, documentId: String): Flow<Result<Map<String, Any>?>> = flow {
        try {
            val document = firestoreService.getDocument(collectionPath, documentId)
            emit(Result.success(document))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun getAllDocuments(collectionPath: String): Flow<Result<QuerySnapshot?>> = flow {
        try {
            val documents = firestoreService.getCollection(collectionPath)
            emit(Result.success(documents))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun addDocument(
        collectionPath: String,
        data: Map<String, Any>,
        documentId: String?
    ): Flow<Result<String?>> = flow {
        try {
            val documentIdResult = firestoreService.addDocument(collectionPath, data, documentId)
            emit(Result.success(documentIdResult))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }


    override suspend fun updateDocument(collectionPath: String, documentId: String, data: Map<String, Any>): Flow<Result<Unit>> = flow {
        try {
            firestoreService.updateDocument(collectionPath, documentId, data)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun deleteDocument(collectionPath: String, documentId: String): Flow<Result<Unit>> = flow {
        try {
            firestoreService.deleteDocument(collectionPath, documentId)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
