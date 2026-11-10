package pe.fulbo.app.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "fulbo_session")

/**
 * Guarda la sesión del usuario.
 * TODO(seguridad): antes de producción, cifrar el token (ej. con Android Keystore / Tink).
 * Nunca guardar el DNI aquí.
 */
@Singleton
class SessionStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val tokenKey = stringPreferencesKey("token")

    val token: Flow<String?> = context.dataStore.data.map { it[tokenKey] }
    val isLoggedIn: Flow<Boolean> = token.map { !it.isNullOrBlank() }

    suspend fun currentToken(): String? = token.first()

    suspend fun saveToken(value: String) {
        context.dataStore.edit { it[tokenKey] = value }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
