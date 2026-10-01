package com.capa8.fitnesspersonalapp.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.capa8.fitnesspersonalapp.navigation.NavRoutes
import com.capa8.fitnesspersonalapp.navigation.sideNavItems
import com.capa8.fitnesspersonalapp.ui.components.FitnessTopBar
import com.capa8.fitnesspersonalapp.ui.components.SideNavPanel
import com.capa8.fitnesspersonalapp.ui.theme.ContentBackground
import com.capa8.fitnesspersonalapp.ui.theme.FitnessPersonalAppTheme

/**
 * Root composable of the app.
 *
 * Layout:
 * ┌──────────────────────────────────────────────────┐
 * │  ☰  FitnessTopBar (blue)                         │
 * ├────────────┬─────────────────────────────────────┤
 * │ SideNav    │  Content area                       │
 * │ 96 dp      │  (fills remaining space)            │
 * │ ──or──     │                                     │
 * │ 52 dp rail │                                     │
 * └────────────┴─────────────────────────────────────┘
 *
 * The hamburger icon (☰) in the top bar and the chevron at the bottom
 * of the side panel both toggle `navCollapsed` — a single shared state
 * owned here at the root level.
 */
@Composable
fun MainScreen() {
    // Track route order for slide direction
    val routeOrder = remember { sideNavItems.map { it.route } }
    var selectedRoute by rememberSaveable { mutableStateOf(NavRoutes.Utilidades.route) }
    var prevRoute     by rememberSaveable { mutableStateOf(NavRoutes.Utilidades.route) }
    var navCollapsed  by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            val currentItem = sideNavItems.find { it.route == selectedRoute }
            val screenTitle = currentItem?.label ?: "FitPoli"
            val sectionIcon = currentItem?.icon
            FitnessTopBar(
                title       = screenTitle,
                sectionIcon = sectionIcon,
                onMenuClick = { navCollapsed = !navCollapsed }
            )
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ContentBackground)
        ) {
            // ── Persistent side navigation ────────────────────────────────
            SideNavPanel(
                items = sideNavItems,
                selectedRoute = selectedRoute,
                onItemClick = { route ->
                    prevRoute = selectedRoute
                    selectedRoute = route
                },
                collapsed = navCollapsed,
                onToggleCollapse = { navCollapsed = !navCollapsed }
            )

            // ── Content area with animated screen transitions ─────────────
            val prevIdx = routeOrder.indexOf(prevRoute)
            val currIdx = routeOrder.indexOf(selectedRoute)
            val slideForward = currIdx >= prevIdx

            AnimatedContent(
                targetState = selectedRoute,
                transitionSpec = {
                    val enter = slideInHorizontally(
                        initialOffsetX = { if (slideForward) it else -it },
                        animationSpec = tween(300)
                    ) + fadeIn(tween(300))
                    val exit = slideOutHorizontally(
                        targetOffsetX = { if (slideForward) -it else it },
                        animationSpec = tween(300)
                    ) + fadeOut(tween(200))
                    enter togetherWith exit
                },
                modifier = Modifier.fillMaxSize(),
                label = "screenTransition"
            ) { route ->
                Box(Modifier.fillMaxSize()) {
                    when (route) {
                        NavRoutes.Perfil.route     -> PerfilScreen()
                        NavRoutes.Fotos.route      -> FotosScreen(modifier = Modifier.fillMaxSize())
                        NavRoutes.Video.route      -> VideoScreen(modifier = Modifier.fillMaxSize())
                        NavRoutes.Web.route        -> WebScreen(modifier = Modifier.fillMaxSize())
                        NavRoutes.Utilidades.route -> UtilidadesScreen()
                        else                       -> PerfilScreen()
                    }
                }
            }
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=360dp,height=800dp,dpi=480"
)
@Composable
private fun MainScreenPreview() {
    FitnessPersonalAppTheme {
        MainScreen()
    }
}
