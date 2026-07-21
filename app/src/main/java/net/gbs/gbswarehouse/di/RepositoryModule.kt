package net.gbs.gbswarehouse.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.gbs.gbswarehouse.data.remote.repository.AuthenticationRepositoryImpl
import net.gbs.gbswarehouse.data.remote.repository.ReceivingRepositoryImpl
import net.gbs.gbswarehouse.domain.repository.AuthenticationRepository
import net.gbs.gbswarehouse.domain.repository.ReceivingRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindReceivingRepository(
        repository: ReceivingRepositoryImpl
    ): ReceivingRepository

    @Binds
    @Singleton
    abstract fun bindAuthenticationRepository(
        repository: AuthenticationRepositoryImpl
    ): AuthenticationRepository

}