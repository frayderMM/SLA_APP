
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
        viewModelScope.launch {
            userPreferences.theme.collect {
                _state.value = _state.value.copy(theme = it ?: "system")
            }
        }
        viewModelScope.launch {
            userPreferences.language.collect {
                _state.value = _state.value.copy(language = it ?: "es")
            }
        }
    }

    fun onThemeChange(theme: String) {
        viewModelScope.launch {
            userPreferences.saveTheme(theme)
        }
    }

    fun onLanguageChange(language: String) {
        viewModelScope.launch {
            userPreferences.saveLanguage(language)
        }
    }
}
