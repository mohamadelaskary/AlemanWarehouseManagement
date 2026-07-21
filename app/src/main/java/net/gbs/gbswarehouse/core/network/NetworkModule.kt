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
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(baseUrlInterceptor)
            .addInterceptor(authInterceptor)
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
    fun provideReceivingApi(
        retrofit: Retrofit
    ): ApiService {
        return retrofit.create(
            ApiService::class.java
        )
    }
}