package dev.esan.sla_app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
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
        title = "Indicadores",
        icon = Icons.Default.Assessment
    )

    object Alertas : BottomNavItem(
        route = Routes.ALERTAS,
        title = "Alertas",
        icon = Icons.Default.Warning
    )

    // Nuevo item para el PDF
    object Pdf : BottomNavItem(
        route = Routes.PDF,
        title = "Reportes",
        icon = Icons.Default.PictureAsPdf
    )

    object Profile : BottomNavItem(
        route = Routes.PROFILE,
        title = "Perfil",
        icon = Icons.Default.Person
    )
}