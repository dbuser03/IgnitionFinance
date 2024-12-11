package com.unimib.ignitionfinance.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface LocalRepository<T> {
    suspend fun getById(id: String): Flow<Result<T?>>
    suspend fun getAll(): Flow<Result<List<T>>>
    suspend fun insert(entity: T): Flow<Result<String>>
    suspend fun update(entity: T): Flow<Result<Unit>>
    suspend fun deleteById(id: Long): Flow<Result<Unit>>
}

class LocalRepositoryImpl<T, DAO> @Inject constructor(
    private val dao: DAO,
    private val insertFn: suspend DAO.(T) -> String,
    private val updateFn: suspend DAO.(T) -> Unit,
    private val deleteByIdFn: suspend DAO.(Long) -> Unit,
    private val getByIdFn: suspend DAO.(String) -> T?,
    private val getAllFn: suspend DAO.() -> List<T>
) : LocalRepository<T> {

    override suspend fun getById(id: String): Flow<Result<T?>> = flow {
        try {
            val result = dao.getByIdFn(id)
            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun getAll(): Flow<Result<List<T>>> = flow {
        try {
            val result = dao.getAllFn()
            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun insert(entity: T): Flow<Result<String>> = flow {
        try {
            val result = dao.insertFn(entity)
            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun update(entity: T): Flow<Result<Unit>> = flow {
        try {
            dao.updateFn(entity)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun deleteById(id: Long): Flow<Result<Unit>> = flow {
        try {
            dao.deleteByIdFn(id)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
