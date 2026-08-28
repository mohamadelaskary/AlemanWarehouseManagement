package net.gbs.gbswarehouse.core.network

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.gbs.gbswarehouse.core.constants.Constants
import net.gbs.gbswarehouse.core.preferences.SettingsPrefs
import net.gbs.gbswarehouse.data.remote.api.ApiService
import net.gbs.gbswarehouse.data.remote.api.AuthInterceptor
import net.gbs.gbswarehouse.data.remote.api.AuthenticationApiService
import net.gbs.gbswarehouse.data.remote.api.TokenAuthenticator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideSettingsPrefs(
        @ApplicationContext context: Context
    ) =
        SettingsPrefs(context)

    @Provides
    @Singleton
    fun provideBaseUrlInterceptor(
        settingsPrefs: SettingsPrefs
    ): BaseUrlInterceptor {
        return BaseUrlInterceptor(settingsPrefs)
    }


    @Provides
    @Singleton
    fun provideOkHttp(
        baseUrlInterceptor: BaseUrlInterceptor,
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .connectTimeout(90, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(90, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(90, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor(baseUrlInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor (logging)
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    @Named("RefreshClient")
    fun provideRefreshClient(
        baseUrlInterceptor: BaseUrlInterceptor,
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor(baseUrlInterceptor).addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor (logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient
    ): Retrofit {

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }
    @Provides
    @Singleton
    @Named("RefreshRetrofit")
    fun provideRefreshRetrofit(
        @Named("RefreshClient")
        client: OkHttpClient
    ): Retrofit {

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    fun provideReceivingApi(
        retrofit: Retrofit
    ): ApiService {
        return retrofit.create(
            ApiService::class.java
        )
    }
    @Provides
    @Singleton
    fun provideRefreshTokenApi(
        @Named("RefreshRetrofit")
        retrofit: Retrofit
    ): AuthenticationApiService {
        return retrofit.create(
            AuthenticationApiService::class.java
        )
    }
}