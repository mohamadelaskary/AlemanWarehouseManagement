package net.gbs.gbswarehouse.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.gbs.gbswarehouse.data.fake.LoadingRepositoryImpl
import net.gbs.gbswarehouse.data.fake.PlcDataSourceImpl
import net.gbs.gbswarehouse.data.remote.repository.AuthenticationRepositoryImpl
import net.gbs.gbswarehouse.data.remote.repository.IssuingRepositoryImpl
import net.gbs.gbswarehouse.data.remote.repository.ReceivingRepositoryImpl
import net.gbs.gbswarehouse.domain.repository.AuthenticationRepository
import net.gbs.gbswarehouse.domain.repository.IssuingRepository
import net.gbs.gbswarehouse.domain.repository.LoadingRepository
import net.gbs.gbswarehouse.domain.repository.PlcDataSource
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

    @Binds
    @Singleton
    abstract fun bindIssuingRepository(
        repository: IssuingRepositoryImpl
    ): IssuingRepository

    @Binds
    @Singleton
    abstract fun bindPlcDataSource(
        dataSource: PlcDataSourceImpl
    ): PlcDataSource

    @Binds
    @Singleton
    abstract fun bindLoadingRepository(
        repository: LoadingRepositoryImpl
    ): LoadingRepository

}
