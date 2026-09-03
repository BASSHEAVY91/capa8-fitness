package com.bassheavy91.fitnesspersonalapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class representing all navigation destinations in the app.
 * Each object defines a unique route string used by Compose Navigation.
 */
sealed class NavRoutes(val route: String) {
    data object Perfil : NavRoutes("perfil")
    data object Fotos  : NavRoutes("fotos")
    data object Video  : NavRoutes("video")
    data object Web    : NavRoutes("web")
}

/**
 * Data class representing a single item in the side navigation menu.
 *
 * @param route  The navigation route this item maps to.
 * @param label  The display label shown when the panel is expanded.
 * @param icon   Material icon shown in both expanded and collapsed (rail) modes.
 */
data class SideNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

/** Ordered list of items shown in the persistent side navigation panel. */
val sideNavItems = listOf(
    SideNavItem(
        route = NavRoutes.Perfil.route,
        label = "Perfil",
        icon  = Icons.Filled.AccountCircle
    ),
    SideNavItem(
        route = NavRoutes.Fotos.route,
        label = "Fotos",
        icon  = Icons.Filled.PhotoLibrary
    ),
    SideNavItem(
        route = NavRoutes.Video.route,
        label = "Video",
        icon  = Icons.Filled.VideoLibrary
    ),
    SideNavItem(
        route = NavRoutes.Web.route,
        label = "Web",
        icon  = Icons.Filled.Language
    )
)
