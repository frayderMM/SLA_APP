package dev.esan.sla_app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun SLAAPPTheme(
    themeIndex: Int,  // ← 0: azul fuerte, 1: celeste, 2: verde, 3: coral
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme = when (themeIndex) {

        // TEMA 1 — AZUL FUERTE
        0 -> lightColorScheme(
            primary = blue_primary,
            onPrimary = blue_onPrimary,
            primaryContainer = blue_primaryContainer,
            onPrimaryContainer = blue_onPrimaryContainer,
            secondary = blue_secondary,
            onSecondary = blue_onSecondary,
            secondaryContainer = blue_secondaryContainer,
            onSecondaryContainer = blue_onSecondaryContainer,
            tertiary = blue_tertiary,
            onTertiary = blue_onTertiary,
            background = blue_background,
            onBackground = blue_onBackground,
            surface = blue_surface,
            onSurface = blue_onSurface,
            surfaceVariant = blue_surfaceVariant,
            onSurfaceVariant = blue_onSurfaceVariant,
            outline = blue_outline
        )

        // TEMA 2 — AZUL CELESTE (DARK)
        1 -> darkColorScheme(
            primary = blueDark_primary,
            onPrimary = blueDark_onPrimary,
            primaryContainer = blueDark_primaryContainer,
            onPrimaryContainer = blueDark_onPrimaryContainer,
            secondary = blueDark_secondary,
            onSecondary = blueDark_onSecondary,
            secondaryContainer = blueDark_secondaryContainer,
            onSecondaryContainer = blueDark_onSecondaryContainer,
            tertiary = blueDark_tertiary,
            onTertiary = blueDark_onTertiary,
            background = blueDark_background,
            onBackground = blueDark_onBackground,
            surface = blueDark_surface,
            onSurface = blueDark_onSurface,
            surfaceVariant = blueDark_surfaceVariant,
            onSurfaceVariant = blueDark_onSurfaceVariant,
            outline = blueDark_outline
        )

        // TEMA 3 — VERDE PROFESIONAL
        2 -> lightColorScheme(
            primary = green_primary,
            onPrimary = green_onPrimary,
            primaryContainer = green_primaryContainer,
            onPrimaryContainer = green_onPrimaryContainer,
            secondary = green_secondary,
            onSecondary = green_onSecondary,
            secondaryContainer = green_secondaryContainer,
            onSecondaryContainer = green_onSecondaryContainer,
            tertiary = green_tertiary,
            onTertiary = green_onTertiary,
            background = green_background,
            onBackground = green_onBackground,
            surface = green_surface,
            onSurface = green_onSurface,
            surfaceVariant = green_surfaceVariant,
            onSurfaceVariant = green_onSurfaceVariant,
            outline = green_outline
        )

        // TEMA 4 — CORAL
        3 -> lightColorScheme(
            primary = coral_primary,
            onPrimary = coral_onPrimary,
            primaryContainer = coral_primaryContainer,
            onPrimaryContainer = coral_onPrimaryContainer,
            secondary = coral_secondary,
            onSecondary = coral_onSecondary,
            secondaryContainer = coral_secondaryContainer,
            onSecondaryContainer = coral_onSecondaryContainer,
            tertiary = coral_tertiary,
            onTertiary = coral_onTertiary,
            background = coral_background,
            onBackground = coral_onBackground,
            surface = coral_surface,
            onSurface = coral_onSurface,
            surfaceVariant = coral_surfaceVariant,
            onSurfaceVariant = coral_onSurfaceVariant,
            outline = coral_outline
        )

        else -> lightColorScheme(primary = blue_primary)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
