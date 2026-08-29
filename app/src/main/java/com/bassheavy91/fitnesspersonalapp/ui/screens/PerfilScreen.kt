package com.bassheavy91.fitnesspersonalapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bassheavy91.fitnesspersonalapp.ui.theme.ContentTextColor
import com.bassheavy91.fitnesspersonalapp.ui.theme.ContentTitleColor
import com.bassheavy91.fitnesspersonalapp.ui.theme.FitnessPersonalAppTheme

/**
 * Perfil screen – shows physical statistics and goals of the user.
 */
@Composable
fun PerfilScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "4. Aplicación de Fitness Personal",
            color = ContentTitleColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 28.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Perfil: Estadísticas físicas y metas del usuario.",
            color = ContentTextColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // TODO: Add user stats cards (weight, height, BMI, goal progress, etc.)
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun PerfilScreenPreview() {
    FitnessPersonalAppTheme {
        PerfilScreen()
    }
}
