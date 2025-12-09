package dev.esan.sla_app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esan.sla_app.data.preferences.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingsViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        // 🔹 TEMA (nuevo themeIndex: Int)
        viewModelScope.launch {
            userPreferences.themeIndex.collect { index ->
                _state.value = _state.value.copy(themeIndex = index)
            }
        }

        // 🔹 IDIOMA
        viewModelScope.launch {
            userPreferences.language.collect { lang ->
                _state.value = _state.value.copy(language = lang)
            }
        }
    }

    // ========== ACTUALIZAR TEMA ==========
    fun onThemeChanged(index: Int) {
        viewModelScope.launch {
            userPreferences.saveThemeIndex(index)
        }
    }

    // ========== ACTUALIZAR IDIOMA ==========
    fun onLanguageChange(language: String) {
        viewModelScope.launch {
            userPreferences.saveLanguage(language)
        }
    }
}
