package com.bassheavy91.fitnesspersonalapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.bassheavy91.fitnesspersonalapp.ui.theme.ContentBackground
import com.bassheavy91.fitnesspersonalapp.ui.theme.FitnessBlue
import com.bassheavy91.fitnesspersonalapp.ui.theme.FitnessPersonalAppTheme

/**
 * Top application bar displaying the app title with the branded blue background.
 *
 * @param title    Text to display as the bar title.
 * @param modifier Optional [Modifier].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessTopBar(
    title: String,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = ContentBackground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
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
        FitnessTopBar(title = "4. Aplicación de Fitness Personal")
    }
}
