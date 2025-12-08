package dev.esan.sla_app.ui.assistant

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("assistant_store")

object ChatStorage {

    private val KEY_MESSAGES = stringPreferencesKey("messages")

    fun loadMessages(context: Context) =
        context.dataStore.data.map { prefs ->
            val json = prefs[KEY_MESSAGES] ?: "[]"

            val type = object : TypeToken<List<ChatMessage>>() {}.type

            val list: List<ChatMessage> = Gson().fromJson(json, type)
            list
        }


    suspend fun saveMessages(context: Context, list: List<ChatMessage>) {
        context.dataStore.edit { prefs ->
            prefs[KEY_MESSAGES] = Gson().toJson(list)
        }
    }

    // Limpieza por inactividad
    suspend fun clear(context: Context) {
        context.dataStore.edit { prefs ->
            prefs[KEY_MESSAGES] = "[]"
        }
    }
}
