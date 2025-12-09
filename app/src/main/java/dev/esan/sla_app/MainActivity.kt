package dev.esan.sla_app

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.compose.rememberNavController
import dev.esan.sla_app.data.preferences.UserPreferences
import dev.esan.sla_app.ui.navigation.AppNavHost
import dev.esan.sla_app.ui.theme.SLAAPPTheme
import java.util.Locale

@Composable
fun UpdatedLocalizedContext(
    language: String,
    content: @Composable () -> Unit
) {
    val currentConfig = LocalConfiguration.current

    val config = remember(language) {
        Configuration(currentConfig).apply {
            val locale = Locale(language)
            setLocale(locale)
            setLayoutDirection(locale)
        }
    }

    CompositionLocalProvider(
        LocalConfiguration provides config
    ) {
        content()
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userPreferences = UserPreferences(this)

        setContent {

            // ============================================================
            // 📌 SE OBTIENE EL ID DEL TEMA (0,1,2,3)
            // ============================================================
            val themeIndex by userPreferences.themeIndex.collectAsState(initial = 0)

            // Idioma
            val language by userPreferences.language.collectAsState(initial = "es")

            UpdatedLocalizedContext(language ?: "es") {

                // ============================================================
                // 🎨 APLICAR EL TEMA SELECCIONADO
                // ============================================================
                SLAAPPTheme(themeIndex = themeIndex) {

                    val navController = rememberNavController()

                    AppNavHost(
                        navController = navController,
                        userPreferences = userPreferences
                    )
                }
            }
        }
    }
}
