package net.gbs.gbswarehouse.data.local

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LoginManagerImpl @Inject constructor(
    @ApplicationContext context: Context
) : LoginManager {
    private val sharedPreferences =
        context.getSharedPreferences(
            "login_pref",
            Context.MODE_PRIVATE
        )

    companion object {
        private const val USER_ID = "user_id"
        private const val USER_COMPANIES = "user_companies"
    }
    override fun getUserId(): String? {
        return sharedPreferences.getString(
            USER_ID,
            null
        )
    }

    override fun getUserCompanies(): List<String> {
        return sharedPreferences.getStringSet(
            USER_COMPANIES,
            null
        ).orEmpty().toList()
    }

    override fun saveUserData(
        userId: String,
        userCompanies: List<String>
    ) {
        sharedPreferences.edit {
            putString(USER_ID, userId)
                .putStringSet(USER_COMPANIES, userCompanies.toSet())
        }
    }

    override fun clear() {
        sharedPreferences.edit {
            putString(USER_ID, null)
                .putStringSet(USER_COMPANIES, setOf<String>())
        }
    }

}