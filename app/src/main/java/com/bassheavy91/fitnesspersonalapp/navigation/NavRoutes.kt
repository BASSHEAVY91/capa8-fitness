package com.bassheavy91.fitnesspersonalapp.navigation

/**
 * Sealed class representing all navigation destinations in the app.
 * Each object defines a unique route string used by Compose Navigation.
 */
sealed class NavRoutes(val route: String) {
    data object Perfil : NavRoutes("perfil")
    data object Fotos : NavRoutes("fotos")
    data object Video : NavRoutes("video")
    data object Web : NavRoutes("web")
}

/**
 * Data class representing a single item in the side navigation menu.
 *
 * @param route     The navigation route this item maps to.
 * @param label     The display label shown in the side menu.
 */
data class SideNavItem(
    val route: String,
    val label: String
)

/** Ordered list of items shown in the persistent side navigation panel. */
val sideNavItems = listOf(
    SideNavItem(route = NavRoutes.Perfil.route, label = "Perfil"),
    SideNavItem(route = NavRoutes.Fotos.route, label = "Fotos"),
    SideNavItem(route = NavRoutes.Video.route, label = "Video"),
    SideNavItem(route = NavRoutes.Web.route, label = "Web")
)
