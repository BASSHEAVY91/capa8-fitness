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
 * Video screen – workout routines and exercises in video format.
 */
@Composable
fun VideoScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Videos de Entrenamiento",
            color = ContentTitleColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 28.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Video: Rutinas y ejercicios en formato de video para el usuario.",
            color = ContentTextColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // TODO: Add video player with workout routine list and categories
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun VideoScreenPreview() {
    FitnessPersonalAppTheme {
        VideoScreen()
    }
}
