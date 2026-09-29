package com.capa8.fitnesspersonalapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** SRP: renders notes section only. Uses PerfilColors — no magic hex. */
@Composable
internal fun PerfilNotesCard(notes: String, onChange: (String) -> Unit) {
    SectionCard(title = "📝 Notas personales") {
        OutlinedTextField(
            value = notes,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
            placeholder = { Text("Añade notas sobre tu progreso, dieta, lesiones…", fontSize = 13.sp, color = PerfilColors.textMuted) },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = PerfilColors.accent,
                unfocusedBorderColor = PerfilColors.border,
                focusedTextColor     = PerfilColors.textPrimary,
                unfocusedTextColor   = PerfilColors.textPrimary,
            ),
            maxLines = 10,
        )
    }
}
