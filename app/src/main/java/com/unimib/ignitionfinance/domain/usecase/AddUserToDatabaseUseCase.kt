package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.local.mapper.UserMapper
import com.unimib.ignitionfinance.data.model.UserData
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddUserToDatabaseUseCase @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val userMapper: UserMapper,
    private val userDataMapper: UserDataMapper,
    private val localDatabaseRepository: LocalDatabaseRepository<User>
) {
    fun execute(collectionPath: String, user: User): Flow<Result<Pair<String?, Unit?>>> = flow {

        val localResult = localDatabaseRepository.add(user).first()

        val userData = userMapper.mapUserToUserData(user)
        val document = userDataMapper.mapUserDataToDocument(userData)
        val documentId = userData.authData.id

        val remoteResult = firestoreRepository.addDocument(collectionPath, document, documentId).first()

        emit(Result.success(Pair(remoteResult.getOrNull(), localResult.getOrNull())))
    }
}