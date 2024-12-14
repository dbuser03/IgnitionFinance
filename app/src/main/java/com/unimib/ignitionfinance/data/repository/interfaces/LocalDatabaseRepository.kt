package com.unimib.ignitionfinance.data.repository.interfaces

import kotlinx.coroutines.flow.Flow

interface LocalDatabaseRepository<T> {
    suspend fun getById(id: String): Flow<Result<T?>>
    suspend fun getAll(): Flow<Result<List<T>>>
    suspend fun add(entity: T): Flow<Result<Unit>>
    suspend fun update(entity: T): Flow<Result<Unit>>
    suspend fun delete(entity: T): Flow<Result<Unit>>
    suspend fun deleteAll(): Flow<Result<Unit>>
}