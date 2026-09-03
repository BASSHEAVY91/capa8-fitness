package com.bassheavy91.fitnesspersonalapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bassheavy91.fitnesspersonalapp.navigation.SideNavItem
import com.bassheavy91.fitnesspersonalapp.navigation.sideNavItems
import com.bassheavy91.fitnesspersonalapp.ui.theme.FitnessBlue
import com.bassheavy91.fitnesspersonalapp.ui.theme.FitnessBlueDark
import com.bassheavy91.fitnesspersonalapp.ui.theme.FitnessPersonalAppTheme
import com.bassheavy91.fitnesspersonalapp.ui.theme.SideMenuBackground
import com.bassheavy91.fitnesspersonalapp.ui.theme.SideMenuSelected
import com.bassheavy91.fitnesspersonalapp.ui.theme.SideMenuText

/**
 * Persistent vertical navigation panel with icons and an animated collapse toggle.
 *
 * Modes:
 *  • **Expanded** (96 dp) — icon + label per item.
 *  • **Collapsed / rail** (52 dp) — icon only.
 *
 * The collapse state is owned by the caller so that the hamburger button in
 * [FitnessTopBar] can also toggle it. The bottom chevron button calls
 * [onToggleCollapse] as well.
 *
 * @param items             Navigation entries to display.
 * @param selectedRoute     Route of the currently active screen.
 * @param onItemClick       Called when the user taps an entry.
 * @param collapsed         Whether the panel is in rail (icon-only) mode.
 * @param onToggleCollapse  Called when the user taps the bottom chevron.
 * @param modifier          Optional modifier for the outer container.
 */
@Composable
fun SideNavPanel(
    items: List<SideNavItem>,
    selectedRoute: String,
    onItemClick: (String) -> Unit,
    collapsed: Boolean = false,
    onToggleCollapse: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Animate the width change smoothly
    val panelWidth by animateDpAsState(
        targetValue = if (collapsed) 52.dp else 96.dp,
        animationSpec = tween(durationMillis = 250),
        label = "sideNavWidth"
    )

    Column(
        modifier = modifier
            .width(panelWidth)
            .fillMaxHeight()
            .background(SideMenuBackground),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(8.dp))

        // ── Nav items ─────────────────────────────────────────────────────────
        items.forEach { navItem ->
            SideNavItemCell(
                item = navItem,
                isSelected = navItem.route == selectedRoute,
                collapsed = collapsed,
                onClick = { onItemClick(navItem.route) }
            )
        }

        Spacer(Modifier.weight(1f))

        // ── Collapse / expand toggle ──────────────────────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleCollapse() }
                .padding(vertical = 12.dp)
        ) {
            Icon(
                imageVector = if (collapsed) Icons.AutoMirrored.Filled.KeyboardArrowRight
                              else Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = if (collapsed) "Expandir menú" else "Contraer menú",
                tint = FitnessBlueDark,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.height(8.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Single item cell
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SideNavItemCell(
    item: SideNavItem,
    isSelected: Boolean,
    collapsed: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) SideMenuSelected else Color.Transparent
    val iconTint = if (isSelected) FitnessBlue else SideMenuText
    val textColor = if (isSelected) FitnessBlueDark else SideMenuText

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp)
    ) {
        // Active indicator bar on the left (only when expanded + selected)
        // We achieve the effect via the background instead.

        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = iconTint,
            modifier = Modifier.size(if (isSelected) 26.dp else 24.dp)
        )

        // Label — hidden in collapsed mode using AnimatedVisibility
        AnimatedVisibility(
            visible = !collapsed,
            enter = expandHorizontally(),
            exit = shrinkHorizontally()
        ) {
            Text(
                text = item.label,
                color = textColor,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 3.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 96, heightDp = 420)
@Composable
private fun SideNavExpandedPreview() {
    FitnessPersonalAppTheme {
        SideNavPanel(
            items = sideNavItems,
            selectedRoute = "video",
            onItemClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 52, heightDp = 420)
@Composable
private fun SideNavCollapsedPreview() {
    FitnessPersonalAppTheme {
        // Simulate collapsed by using a narrow preview width
        SideNavPanel(
            items = sideNavItems,
            selectedRoute = "video",
            onItemClick = {}
        )
    }
}
