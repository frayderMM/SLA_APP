package dev.esan.sla_app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.SmartToy // <-- IMPORT AÑADIDO
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : BottomNavItem(
        route = Routes.DASHBOARD,
        title = "Dashboard",
        icon = Icons.Default.Home
    )

    object Indicadores : BottomNavItem(
        route = Routes.INDICADORES,
        title = "Solicitudes",
        icon = Icons.Default.Assessment
    )

    object Profile : BottomNavItem(
        route = Routes.PROFILE,
        title = "Perfil",
        icon = Icons.Default.Person
    )

    // CORREGIDO: Se usa un icono más apropiado para el asistente
    object Assistant : BottomNavItem(
        route = Routes.ASSISTANT,
        title = "Assistant",
        icon = Icons.Default.SmartToy 
    )
}
