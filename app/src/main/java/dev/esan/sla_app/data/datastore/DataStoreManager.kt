package dev.esan.sla_app.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import dev.esan.sla_app.data.model.AuthenticatedUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val context: Context) {

    companion object {
        private val Context.dataStore by preferencesDataStore("damsla_prefs")
        val USER_KEY = stringPreferencesKey("authenticated_user")
        val TOKEN_KEY = stringPreferencesKey("jwt_token") // Keep for get token flow
    }

    private val gson = Gson()

    val token: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[TOKEN_KEY]
    }

    suspend fun saveUser(user: AuthenticatedUser) {
        context.dataStore.edit { prefs ->
            prefs[USER_KEY] = gson.toJson(user)
            prefs[TOKEN_KEY] = user.token // Also save raw token for interceptor
        }
    }

    suspend fun clearUser() {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_KEY)
            prefs.remove(TOKEN_KEY)
        }
    }

    fun getAuthenticatedUserFlow(): Flow<AuthenticatedUser?> {
        return context.dataStore.data.map { preferences ->
            val userJson = preferences[USER_KEY]
            if (userJson != null) {
                gson.fromJson(userJson, AuthenticatedUser::class.java)
            } else {
                // Fallback for old JWT decoding logic if needed, or just return null
                val token = preferences[TOKEN_KEY]
                if (token != null) {
                    try {
                        val jwt = JWT(token)
                        AuthenticatedUser(
                            token = token,
                            id = jwt.subject ?: "",
                            email = jwt.getClaim("email").asString() ?: "",
                            role = jwt.getClaim("rol").asString() ?: "Usuario",
                            nombre = jwt.getClaim("nombre").asString()
                        )
                    } catch (e: Exception) {
                        null
                    }
                } else {
                    null
                }
            }
        }
    }
}