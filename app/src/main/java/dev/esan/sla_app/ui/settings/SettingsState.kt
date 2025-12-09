package dev.esan.sla_app.ui.settings

data class SettingsState(
    val themeIndex: Int = 0,   // 0 = Azul fuerte (por defecto)
    val language: String = "es"
)
