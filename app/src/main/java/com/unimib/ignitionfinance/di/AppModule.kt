package com.unimib.ignitionfinance.di

import com.unimib.ignitionfinance.data.remote.mapper.AuthMapper
import com.unimib.ignitionfinance.data.remote.service.AuthService
import com.unimib.ignitionfinance.data.repository.AuthRepository
import com.unimib.ignitionfinance.data.repository.AuthRepositoryImpl
import com.unimib.ignitionfinance.domain.usecase.LoginUserUseCase
import com.unimib.ignitionfinance.domain.usecase.RegisterNewUserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideAuthService(): AuthService {
        return AuthService()
    }

    @Provides
    fun provideAuthMapper(): AuthMapper {
        return AuthMapper
    }

    @Provides
    fun provideAuthRepository(
        authService: AuthService,
        authMapper: AuthMapper
    ): AuthRepository = AuthRepositoryImpl(authService, authMapper)

    @Provides
    fun provideRegisterNewUserUseCase(authRepository: AuthRepository): RegisterNewUserUseCase {
        return RegisterNewUserUseCase(authRepository)
    }

    @Provides
    fun provideLoginUserUseCase(authRepository: AuthRepository): LoginUserUseCase {
        return LoginUserUseCase(authRepository)
    }
}
