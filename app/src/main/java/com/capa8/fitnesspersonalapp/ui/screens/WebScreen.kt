package com.capa8.fitnesspersonalapp.ui.screens

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
import com.capa8.fitnesspersonalapp.ui.theme.ContentTextColor
import com.capa8.fitnesspersonalapp.ui.theme.ContentTitleColor
import com.capa8.fitnesspersonalapp.ui.theme.FitnessPersonalAppTheme

/**
 * Web screen – online fitness and nutrition articles and guides.
 */
@Composable
fun WebScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Recursos Web",
            color = ContentTitleColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 28.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Web: Artículos, guías y recursos online de fitness y nutrición.",
            color = ContentTextColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // TODO: Add WebView or curated list of fitness resource links
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun WebScreenPreview() {
    FitnessPersonalAppTheme {
        WebScreen()
    }
}
