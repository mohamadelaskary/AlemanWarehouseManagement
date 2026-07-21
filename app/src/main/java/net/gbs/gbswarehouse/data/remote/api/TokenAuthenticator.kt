package net.gbs.gbswarehouse.data.remote.api

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.gbs.gbswarehouse.data.local.TokenManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject


class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApi: ApiService
) : Authenticator {

    private val mutex = Mutex()
    override fun authenticate(
        route: Route?,
        response: Response
    ): Request? {
        runBlocking {

            mutex.withLock {

                // Refresh Logic

            }

        }
        return null
    }
}