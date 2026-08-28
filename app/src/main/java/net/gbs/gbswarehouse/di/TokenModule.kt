package net.gbs.gbswarehouse.di


import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.gbs.gbswarehouse.data.local.LoginManager
import net.gbs.gbswarehouse.data.local.LoginManagerImpl
import net.gbs.gbswarehouse.data.local.TokenManager
import net.gbs.gbswarehouse.data.local.TokenManagerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TokenModule {

    @Provides
    @Singleton
    fun provideTokenManager(
        @ApplicationContext context: Context
    ): TokenManager {
        return TokenManagerImpl(context)
    }

    @Provides
    @Singleton
    fun provideLoginManager(@ApplicationContext context: Context): LoginManager {
        return LoginManagerImpl(context)
    }

}