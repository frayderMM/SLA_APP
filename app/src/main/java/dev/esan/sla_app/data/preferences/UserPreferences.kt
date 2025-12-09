package dev.esan.sla_app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión para el DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferences(private val context: Context) {

    // =====================================================
    // 💠 KEYS
    // =====================================================

    private val themeIndexKey = intPreferencesKey("themeIndex") // ← NUEVO (0–3)
    private val languageKey = stringPreferencesKey("language")  // idioma

    // =====================================================
    // 🔵 GETTERS — Flows observables
    // =====================================================

    // 0: Azul fuerte
    // 1: Azul celeste (dark)
    // 2: Verde profesional
    // 3: Coral premium
    val themeIndex: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[themeIndexKey] ?: 0  // Por defecto tema azul fuerte
    }

    val language: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[languageKey] ?: "es"
    }

    // =====================================================
    // 🟢 SETTERS — Guardar valores
    // =====================================================

    suspend fun saveThemeIndex(index: Int) {
        context.dataStore.edit { prefs ->
            prefs[themeIndexKey] = index
        }
    }

    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { prefs ->
            prefs[languageKey] = language
        }
    }
}
