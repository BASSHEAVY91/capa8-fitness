package com.capa8.fitnesspersonalapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * SRP: pure rendering — zero state, zero SharedPreferences, zero business logic.
 * All state lives in PerfilViewModel; this file is just a wiring layer.
 * Clean Code: ≤ 100 lines, descriptive names, no magic values.
 */
@Composable
fun PerfilScreen(vm: PerfilViewModel = viewModel()) {
    val wfc = whiteFieldColors()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PerfilColors.surface)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        PerfilHeaderCard(
            profile    = vm.profile,
            tmpProfile = vm.tmpProfile,
            editMode   = vm.editMode,
            wfc        = wfc,
            onTmpChange = vm::updateTmp,
            onPhotoPath = vm::setPhotoPath,
            onEdit     = vm::startEdit,
            onSave     = vm::saveProfile,
            onCancel   = vm::cancelEdit,
        )
        PerfilGoalsCard(
            goals   = vm.goals,
            checked = vm.checked,
            onToggle = vm::toggleGoal,
            onRemove = vm::removeGoal,
            onAdd    = vm::addGoal,
        )
        PerfilScheduleCard(
            schedule = vm.schedule,
            onRemove = vm::removeScheduleEntry,
            onAdd    = vm::addScheduleEntry,
        )
        PerfilNotesCard(
            notes    = vm.notes,
            onChange = vm::updateNotes,
        )
        Spacer(Modifier.height(16.dp))
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

/** White-text field colors used inside the blue gradient header card. */
@Composable
internal fun whiteFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = Color.White,
    unfocusedBorderColor    = Color.White.copy(.6f),
    focusedLabelColor       = Color.White,
    unfocusedLabelColor     = Color.White.copy(.8f),
    focusedTextColor        = Color.White,
    unfocusedTextColor      = Color.White,
    cursorColor             = Color.White,
)

/** Compact stat chip for the profile header display row. */
@Composable
internal fun StatChip(value: String, label: String) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = Color.White)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(.75f))
    }
}
