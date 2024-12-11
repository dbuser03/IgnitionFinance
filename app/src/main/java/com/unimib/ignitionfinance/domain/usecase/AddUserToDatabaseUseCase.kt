package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.model.UserData
import com.unimib.ignitionfinance.data.remote.mapper.UserMapper
import com.unimib.ignitionfinance.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddUserToDatabaseUseCase @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val userMapper: UserMapper
) {
    suspend fun execute(collectionPath: String, userData: UserData): Flow<Result<String?>> {
        val documentId = userData.authData.id
        val dataMap = userMapper.mapUserToDocument(userData)
        return firestoreRepository.addDocument(collectionPath, dataMap, documentId)
    }
}