package com.capa8.fitnesspersonalapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
 * of the side panel both toggle [navCollapsed] — a single shared state
 * owned here at the root level.
 */
@Composable
fun MainScreen() {
    var selectedRoute by rememberSaveable { mutableStateOf(NavRoutes.Perfil.route) }
    var navCollapsed  by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            FitnessTopBar(
                title = "4. Aplicación de Fitness Personal",
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
                onItemClick = { route -> selectedRoute = route },
                collapsed = navCollapsed,
                onToggleCollapse = { navCollapsed = !navCollapsed }
            )

            // ── Content area ──────────────────────────────────────────────
            val contentModifier = Modifier.fillMaxSize()

            when (selectedRoute) {
                NavRoutes.Perfil.route -> PerfilScreen(modifier = contentModifier)
                NavRoutes.Fotos.route  -> FotosScreen(modifier = contentModifier)
                NavRoutes.Video.route  -> VideoScreen(modifier = contentModifier)
                NavRoutes.Web.route    -> WebScreen(modifier = contentModifier)
                else                   -> PerfilScreen(modifier = contentModifier)
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
