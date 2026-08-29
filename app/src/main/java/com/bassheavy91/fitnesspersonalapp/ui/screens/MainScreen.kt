package com.bassheavy91.fitnesspersonalapp.ui.screens

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
import com.bassheavy91.fitnesspersonalapp.navigation.NavRoutes
import com.bassheavy91.fitnesspersonalapp.navigation.sideNavItems
import com.bassheavy91.fitnesspersonalapp.ui.components.FitnessTopBar
import com.bassheavy91.fitnesspersonalapp.ui.components.SideNavPanel
import com.bassheavy91.fitnesspersonalapp.ui.theme.ContentBackground
import com.bassheavy91.fitnesspersonalapp.ui.theme.FitnessPersonalAppTheme

/**
 * Root composable of the app.
 *
 * Layout:
 * ┌─────────────────────────────────────────┐
 * │           FitnessTopBar (blue)          │
 * ├──────────┬──────────────────────────────┤
 * │SideNav   │  Content area                │
 * │(100 dp)  │  (screen fills remaining)    │
 * │          │                              │
 * └──────────┴──────────────────────────────┘
 */
@Composable
fun MainScreen() {
    // Persist selected route across recompositions and process death
    var selectedRoute by rememberSaveable { mutableStateOf(NavRoutes.Perfil.route) }

    Scaffold(
        topBar = {
            FitnessTopBar(title = "4. Aplicación de Fitness Personal")
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
                onItemClick = { route -> selectedRoute = route }
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
