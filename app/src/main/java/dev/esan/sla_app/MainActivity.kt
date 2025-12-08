
package dev.esan.sla_app

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import dev.esan.sla_app.data.preferences.UserPreferences
import dev.esan.sla_app.ui.navigation.AppNavHost
import dev.esan.sla_app.ui.theme.SLAAPPTheme
import java.util.Locale

@Composable
fun UpdatedLocalizedContext(language: String, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val updatedContext = remember(language) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        context.createConfigurationContext(configuration)
    }
    CompositionLocalProvider(LocalContext provides updatedContext) {
        content()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userPreferences = UserPreferences(this)

        setContent {
            val theme by userPreferences.theme.collectAsState(initial = "system")
            val language by userPreferences.language.collectAsState(initial = "es")

            UpdatedLocalizedContext(language = language ?: "es") {
                val useDarkTheme = when (theme) {
                    "light" -> false
                    "dark" -> true
                    else -> isSystemInDarkTheme()
                }

                SLAAPPTheme(darkTheme = useDarkTheme) {
                    val navController = rememberNavController()
                    AppNavHost(navController = navController, userPreferences = userPreferences)
                }
            }
        }
    }
}
