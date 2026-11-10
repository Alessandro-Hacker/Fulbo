package pe.fulbo.app.core.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import pe.fulbo.app.core.datastore.SessionStore
import javax.inject.Inject

/**
 * Agrega el token de sesión a cada petición.
 * TODO(auth): agregar un okhttp3.Authenticator que renueve el token al recibir 401.
 */
class AuthInterceptor @Inject constructor(
    private val session: SessionStore,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { session.currentToken() }
        val request = chain.request().newBuilder().apply {
            if (token != null) header("Authorization", "Bearer $token")
            header("Accept-Language", "es-PE")
        }.build()
        return chain.proceed(request)
    }
}
