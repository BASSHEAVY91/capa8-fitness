package com.capa8.fitnesspersonalapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.capa8.fitnesspersonalapp.ui.theme.ContentBackground
import com.capa8.fitnesspersonalapp.ui.theme.FitnessBlue
import com.capa8.fitnesspersonalapp.ui.theme.FitnessPersonalAppTheme

/**
 * Top application bar with branded blue background.
 *
 * @param title       Text displayed as the bar title.
 * @param onMenuClick Callback invoked when the hamburger (☰) icon is tapped.
 *                    Pass `null` to hide the icon (default).
 * @param modifier    Optional [Modifier].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessTopBar(
    title: String,
    onMenuClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        navigationIcon = {
            // Show the hamburger button only when a callback is provided
            if (onMenuClick != null) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menú",
                        tint = ContentBackground
                    )
                }
            }
        },
        title = {
            Text(
                text = title,
                color = ContentBackground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                // When there's a nav icon, left-align; otherwise centre
                textAlign = if (onMenuClick != null) TextAlign.Start else TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = FitnessBlue,
            titleContentColor = ContentBackground
        ),
        modifier = modifier
    )
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun FitnessTopBarPreview() {
    FitnessPersonalAppTheme {
        FitnessTopBar(
            title = "4. Aplicación de Fitness Personal",
            onMenuClick = {}
        )
    }
}
