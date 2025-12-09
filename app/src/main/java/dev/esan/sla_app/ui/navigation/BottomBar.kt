package dev.esan.sla_app.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavHostController) {

    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Indicadores,
        BottomNavItem.Assistant,
        BottomNavItem.Profile
    )

    val colorScheme = MaterialTheme.colorScheme

    NavigationBar(
        containerColor = colorScheme.surface,
        tonalElevation = 8.dp
    ) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination: NavDestination? = navBackStackEntry?.destination

        items.forEach { item ->

            val selected = currentDestination?.route == item.route

            // ⭐ Animación del color del icono y texto
            val animatedColor by animateColorAsState(
                targetValue = if (selected)
                    colorScheme.primary
                else
                    colorScheme.onSurfaceVariant,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = ""
            )

            // ⭐ Animación del tamaño (efecto rebote)
            val scale by animateFloatAsState(
                targetValue = if (selected) 1.2f else 1.0f,
                animationSpec = spring(
                    stiffness = Spring.StiffnessMedium,
                    dampingRatio = Spring.DampingRatioLowBouncy
                ),
                label = ""
            )

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = animatedColor,
                        modifier = Modifier.size((26 * scale).dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = animatedColor
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Unspecified,  // manejado manualmente
                    selectedTextColor = Color.Unspecified,
                    unselectedIconColor = Color.Unspecified,
                    unselectedTextColor = Color.Unspecified,
                    indicatorColor = colorScheme.surfaceVariant
                )
            )
        }
    }
}
