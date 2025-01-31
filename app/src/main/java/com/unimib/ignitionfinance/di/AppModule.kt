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
import com.unimib.ignitionfinance.data.remote.mapper.ExchangeMapper
import com.unimib.ignitionfinance.data.remote.mapper.InflationMapper
import com.unimib.ignitionfinance.data.remote.mapper.SearchStockMapper
import com.unimib.ignitionfinance.data.remote.mapper.StockMapper
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.data.remote.service.AuthService
import com.unimib.ignitionfinance.data.remote.service.ExchangeService
import com.unimib.ignitionfinance.data.remote.service.FirestoreService
import com.unimib.ignitionfinance.data.remote.service.InflationService
import com.unimib.ignitionfinance.data.remote.service.SearchStockService
import com.unimib.ignitionfinance.data.remote.service.StockService
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.data.repository.implementation.AuthRepositoryImpl
import com.unimib.ignitionfinance.data.repository.implementation.ExchangeRepositoryImpl
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.implementation.FirestoreRepositoryImpl
import com.unimib.ignitionfinance.data.repository.implementation.InflationRepositoryImpl
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.implementation.LocalDatabaseRepositoryImpl
import com.unimib.ignitionfinance.data.repository.implementation.SearchStockRepositoryImpl
import com.unimib.ignitionfinance.data.repository.implementation.StockRepositoryImpl
import com.unimib.ignitionfinance.data.repository.implementation.SyncQueueItemRepositoryImpl
import com.unimib.ignitionfinance.data.repository.implementation.UserPreferencesRepositoryImpl
import com.unimib.ignitionfinance.data.repository.interfaces.ExchangeRepository
import com.unimib.ignitionfinance.data.repository.interfaces.InflationRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SearchStockRepository
import com.unimib.ignitionfinance.data.repository.interfaces.StockRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import com.unimib.ignitionfinance.data.repository.interfaces.UserPreferencesRepository
import com.unimib.ignitionfinance.data.worker.SyncWorkerFactory
import com.unimib.ignitionfinance.domain.usecase.auth.AddUserToDatabaseUseCase
import com.unimib.ignitionfinance.domain.usecase.settings.GetUserSettingsUseCase
import com.unimib.ignitionfinance.domain.usecase.auth.LoginUserUseCase
import com.unimib.ignitionfinance.domain.usecase.auth.RegisterNewUserUseCase
import com.unimib.ignitionfinance.domain.usecase.auth.ResetPasswordUseCase
import com.unimib.ignitionfinance.domain.usecase.settings.UpdateUserSettingsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideGson(): Gson = Gson()

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson, baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    fun provideAuthService(): AuthService = AuthService()

    @Provides
    fun provideFirestoreService(): FirestoreService = FirestoreService()

    @Provides
    fun provideExchangeService(okHttpClient: OkHttpClient, gson: Gson): ExchangeService {
        return provideRetrofit(okHttpClient, gson, "https://data-api.ecb.europa.eu/").create(
            ExchangeService::class.java
        )
    }

    @Provides
    fun provideInflationService(okHttpClient: OkHttpClient, gson: Gson): InflationService {
        return provideRetrofit(okHttpClient, gson, "https://data-api.ecb.europa.eu/").create(
            InflationService::class.java
        )
    }

    @Provides
    fun provideStockService(okHttpClient: OkHttpClient, gson: Gson): StockService {
        return provideRetrofit(okHttpClient, gson, "https://www.alphavantage.co/").create(
            StockService::class.java
        )
    }

    @Provides
    fun provideSearchStockService(okHttpClient: OkHttpClient, gson: Gson): SearchStockService {
        return provideRetrofit(okHttpClient, gson, "https://www.alphavantage.co/").create(
            SearchStockService::class.java
        )
    }

    @Provides
    fun provideSearchStockMapper(): SearchStockMapper = SearchStockMapper

    @Provides
    fun provideStockMapper(): StockMapper = StockMapper

    @Provides
    fun provideInflationMapper(): InflationMapper = InflationMapper

    @Provides
    fun provideAuthMapper(): AuthMapper = AuthMapper

    @Provides
    fun provideUserMapper(): UserMapper = UserMapper

    @Provides
    fun provideUserDataMapper(): UserDataMapper = UserDataMapper

    @Provides
    fun provideExchangeMapper(): ExchangeMapper = ExchangeMapper

    @Provides
    fun provideAuthRepository(
        authService: AuthService,
        authMapper: AuthMapper
    ): AuthRepository = AuthRepositoryImpl(authService, authMapper)

    @Provides
    fun provideExchangeRepository(
        exchangeService: ExchangeService,
        exchangeMapper: ExchangeMapper
    ): ExchangeRepository = ExchangeRepositoryImpl(exchangeService, exchangeMapper)

    @Provides
    fun provideInflationRepository(
        inflationService: InflationService,
        inflationMapper: InflationMapper
    ): InflationRepository = InflationRepositoryImpl(inflationService, inflationMapper)

    @Provides
    fun provideSearchStockRepository(
        searchStockService: SearchStockService,
        searchStockMapper: SearchStockMapper
    ): SearchStockRepository = SearchStockRepositoryImpl(searchStockService, searchStockMapper)

    @Provides
    fun provideStockRepository(
        stockService: StockService,
        stockMapper: StockMapper
    ): StockRepository = StockRepositoryImpl(stockService, stockMapper)

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
    fun provideUserPreferencesRepository(
        @ApplicationContext context: Context
    ): UserPreferencesRepository {
        return UserPreferencesRepositoryImpl(context)
    }

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
    fun provideUpdateUserSettingsUseCase(
        authRepository: AuthRepository,
        userMapper: UserMapper,
        userDataMapper: UserDataMapper,
        localDatabaseRepository: LocalDatabaseRepository<User>,
        syncQueueItemRepository: SyncQueueItemRepository,
        getUserSettingsUseCase: GetUserSettingsUseCase,
        context: Context
    ): UpdateUserSettingsUseCase = UpdateUserSettingsUseCase(
        authRepository,
        userMapper,
        userDataMapper,
        localDatabaseRepository,
        syncQueueItemRepository,
        getUserSettingsUseCase,
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