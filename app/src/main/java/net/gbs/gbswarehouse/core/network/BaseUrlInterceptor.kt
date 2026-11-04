package net.gbs.gbswarehouse.core.network

import net.gbs.gbswarehouse.core.constants.Constants
import net.gbs.gbswarehouse.core.preferences.SettingsPrefs
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject

class BaseUrlInterceptor @Inject constructor(
    private val settingsPrefs: SettingsPrefs
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        val currentUrl = request.url

        val baseUrl = runCatching {
            settingsPrefs.getBaseUrl().toHttpUrl()
        }.getOrNull() ?: Constants.BASE_URL.toHttpUrl()

        val newUrl = currentUrl.newBuilder()
            .scheme(baseUrl.scheme)
            .host(baseUrl.host)
            .port(baseUrl.port)
            .build()

        val newRequest = request.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}