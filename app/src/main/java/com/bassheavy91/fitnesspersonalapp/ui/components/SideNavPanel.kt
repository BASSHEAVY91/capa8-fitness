package com.bassheavy91.fitnesspersonalapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bassheavy91.fitnesspersonalapp.navigation.SideNavItem
import com.bassheavy91.fitnesspersonalapp.navigation.sideNavItems
import com.bassheavy91.fitnesspersonalapp.ui.theme.FitnessPersonalAppTheme
import com.bassheavy91.fitnesspersonalapp.ui.theme.SideMenuBackground
import com.bassheavy91.fitnesspersonalapp.ui.theme.SideMenuSelected
import com.bassheavy91.fitnesspersonalapp.ui.theme.SideMenuText

/**
 * Persistent vertical navigation panel shown on the left side of the screen.
 *
 * @param items         List of [SideNavItem] entries to display.
 * @param selectedRoute The route of the currently selected item.
 * @param onItemClick   Callback invoked when the user taps a menu item.
 * @param modifier      Optional [Modifier] for the panel container.
 */
@Composable
fun SideNavPanel(
    items: List<SideNavItem>,
    selectedRoute: String,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .width(100.dp)
            .fillMaxHeight()
            .background(SideMenuBackground)
    ) {
        items(items) { navItem ->
            SideNavItemRow(
                item = navItem,
                isSelected = navItem.route == selectedRoute,
                onClick = { onItemClick(navItem.route) }
            )
        }
    }
}

/**
 * Single row inside [SideNavPanel].
 * Highlights with a darker background when [isSelected] is true.
 */
@Composable
private fun SideNavItemRow(
    item: SideNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(if (isSelected) SideMenuSelected else SideMenuBackground)
            .clickable(onClick = onClick)
            .padding(vertical = 24.dp, horizontal = 8.dp)
    ) {
        Text(
            text = item.label,
            color = SideMenuText,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 100, heightDp = 400)
@Composable
private fun SideNavPanelPreview() {
    FitnessPersonalAppTheme {
        SideNavPanel(
            items = sideNavItems,
            selectedRoute = "perfil",
            onItemClick = {}
        )
    }
}
