package net.gbs.gbswarehouse.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.core.content.edit

class TokenManagerImpl @Inject constructor(
    @ApplicationContext context: Context
) : TokenManager {

    private val sharedPreferences =
        context.getSharedPreferences(
            "auth_pref",
            Context.MODE_PRIVATE
        )

    companion object {
        private const val ACCESS_TOKEN = "access_token"
        private const val REFRESH_TOKEN = "refresh_token"
    }

    override fun getAccessToken(): String? {
        return sharedPreferences.getString(
            ACCESS_TOKEN,
            null
        )
    }

    override fun getRefreshToken(): String? {
        return sharedPreferences.getString(
            REFRESH_TOKEN,
            null
        )
    }

    override fun saveTokens(
        accessToken: String,
        refreshToken: String
    ) {
        sharedPreferences.edit {
            putString(ACCESS_TOKEN, accessToken)
                .putString(REFRESH_TOKEN, refreshToken)
        }
    }

    override fun clear() {
        sharedPreferences.edit { clear() }
    }
}