package dev.esan.sla_app.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.auth0.android.jwt.JWT
import dev.esan.sla_app.data.model.AuthenticatedUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.lang.Exception

class DataStoreManager(private val context: Context) {

    companion object {
        private val Context.dataStore by preferencesDataStore("damsla_prefs")
        val TOKEN_KEY = stringPreferencesKey("jwt_token")
    }

    val token: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[TOKEN_KEY]
    }

    suspend fun saveToken(value: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = value
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }

    // ✨ NUEVA FUNCIÓN PARA DECODIFICAR EL TOKEN ✨
    fun getAuthenticatedUserFlow(): Flow<AuthenticatedUser?> {
        return context.dataStore.data.map { preferences ->
            val token = preferences[TOKEN_KEY] ?: return@map null
            try {
                val jwt = JWT(token)
                AuthenticatedUser(
                    id = jwt.subject ?: "", // "sub" es el ID del usuario
                    email = jwt.getClaim("email").asString() ?: "",
                    role = jwt.getClaim("rol").asString() ?: "Usuario", // Ajusta el nombre del claim si es diferente
                    nombre = jwt.getClaim("nombre").asString() // Ajusta si existe
                )
            } catch (e: Exception) {
                // El token es inválido o está malformado
                null
            }
        }
    }
}