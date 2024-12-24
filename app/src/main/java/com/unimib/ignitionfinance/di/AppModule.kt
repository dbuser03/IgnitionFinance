package com.unimib.ignitionfinance.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.unimib.ignitionfinance.data.local.database.AppDatabase
import com.unimib.ignitionfinance.data.local.database.SyncQueueItemDao
import com.unimib.ignitionfinance.data.local.database.UserDao
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.local.mapper.UserMapper
import com.unimib.ignitionfinance.data.remote.mapper.AuthMapper
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.data.remote.service.AuthService
import com.unimib.ignitionfinance.data.remote.service.FirestoreService
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.data.repository.implementation.AuthRepositoryImpl
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.implementation.FirestoreRepositoryImpl
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.implementation.LocalDatabaseRepositoryImpl
import com.unimib.ignitionfinance.data.repository.implementation.SyncQueueItemRepositoryImpl
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import com.unimib.ignitionfinance.data.worker.SyncWorkerFactory
import com.unimib.ignitionfinance.domain.usecase.AddUserToDatabaseUseCase
import com.unimib.ignitionfinance.domain.usecase.LoginUserUseCase
import com.unimib.ignitionfinance.domain.usecase.RegisterNewUserUseCase
import com.unimib.ignitionfinance.domain.usecase.ResetPasswordUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideGson(): Gson = Gson()

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    fun provideAuthService(): AuthService = AuthService()

    @Provides
    fun provideFirestoreService(): FirestoreService = FirestoreService()

    @Provides
    fun provideAuthMapper(): AuthMapper = AuthMapper

    @Provides
    fun provideUserMapper(): UserMapper = UserMapper

    @Provides
    fun provideUserDataMapper(): UserDataMapper = UserDataMapper

    @Provides
    fun provideAuthRepository(
        authService: AuthService,
        authMapper: AuthMapper
    ): AuthRepository = AuthRepositoryImpl(authService, authMapper)

    @Provides
    fun provideFirestoreRepository(
        firestoreService: FirestoreService
    ): FirestoreRepository = FirestoreRepositoryImpl(firestoreService)

    @Provides
    fun provideSyncQueueItemRepository(
        syncQueueItemDao: SyncQueueItemDao
    ): SyncQueueItemRepository = SyncQueueItemRepositoryImpl(syncQueueItemDao)

    @Provides
    fun provideLocalDatabaseRepository(
        userDao: UserDao
    ): LocalDatabaseRepository<User> = LocalDatabaseRepositoryImpl(
        dao = userDao,
        addFn = UserDao::add,
        updateFn = UserDao::update,
        deleteFn = UserDao::delete,
        getByIdFn = UserDao::getUserById,
        getAllFn = UserDao::getAllUsers,
        deleteAllFn = UserDao::deleteAllUsers,
        getUnsyncedFn = UserDao::getUnsyncedUsers,
        getUpdatedAfterFn = UserDao::getUsersUpdatedAfter,
        updateLastSyncFn = UserDao::updateLastSyncTimestamp
    )

    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            name = "ignition_finance_database"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao =
        appDatabase.userDao()

    @Provides
    fun provideSyncItemQueueDao(appDatabase: AppDatabase): SyncQueueItemDao =
        appDatabase.syncQueueItemDao()

    @Provides
    fun provideLoginUserUseCase(authRepository: AuthRepository): LoginUserUseCase =
        LoginUserUseCase(authRepository)

    @Provides
    fun provideResetPasswordUseCase(authRepository: AuthRepository): ResetPasswordUseCase =
        ResetPasswordUseCase(authRepository)

    @Provides
    fun provideRegisterNewUserUseCase(authRepository: AuthRepository): RegisterNewUserUseCase =
        RegisterNewUserUseCase(authRepository)

    @Provides
    fun provideAddUserToDatabaseUseCase(
        userMapper: UserMapper,
        userDataMapper: UserDataMapper,
        localDatabaseRepository: LocalDatabaseRepository<User>,
        firestoreRepository: FirestoreRepository,
        syncQueueItemRepository: SyncQueueItemRepository,
        context: Context
    ): AddUserToDatabaseUseCase = AddUserToDatabaseUseCase(
        userMapper,
        userDataMapper,
        localDatabaseRepository,
        firestoreRepository,
        syncQueueItemRepository,
        context
    )

    @Provides
    fun provideSyncWorkerFactory(
        syncQueueItemRepository: SyncQueueItemRepository,
        firestoreRepository: FirestoreRepository,
        localDatabaseRepository: LocalDatabaseRepository<User>
    ): SyncWorkerFactory = SyncWorkerFactory(
        syncQueueItemRepository = syncQueueItemRepository,
        firestoreRepository = firestoreRepository,
        localDatabaseRepository = localDatabaseRepository
    )
}