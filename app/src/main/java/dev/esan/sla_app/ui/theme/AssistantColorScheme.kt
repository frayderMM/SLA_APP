package dev.esan.sla_app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme

// 🔥 Fondo dinámico con degradado según el tema
@Composable
fun assistantBackground(): Brush {
    val c = MaterialTheme.colorScheme

    return Brush.verticalGradient(
        listOf(
            c.primary.copy(alpha = 0.22f),
            c.primary.copy(alpha = 0.55f),
            c.primary.copy(alpha = 0.85f)
        )
    )
}

// 🎤 Botón de micrófono / enviar
@Composable
fun assistantAccent(): Color {
    return MaterialTheme.colorScheme.primary
}

// 💬 Burbuja del usuario
@Composable
fun assistantUserBubble(): Color {
    return MaterialTheme.colorScheme.primary
}

// 💬 Burbuja del bot
@Composable
fun assistantBotBubble(): Color {
    val c = MaterialTheme.colorScheme
    return c.surfaceVariant.copy(alpha = 0.65f)
}

// ⚠ Burbuja de error
@Composable
fun assistantErrorBubble(): Color {
    return MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
}
