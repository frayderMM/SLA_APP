
package dev.esan.sla_app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val themeKey = stringPreferencesKey("theme")
    private val languageKey = stringPreferencesKey("language")

    val theme: Flow<String?> = context.dataStore.data.map {
        it[themeKey]
    }

    val language: Flow<String?> = context.dataStore.data.map {
        it[languageKey]
    }

    suspend fun saveTheme(theme: String) {
        context.dataStore.edit {
            it[themeKey] = theme
        }
    }

    suspend fun saveLanguage(language: String) {
        context.dataStore.edit {
            it[languageKey] = language
        }
    }
}
