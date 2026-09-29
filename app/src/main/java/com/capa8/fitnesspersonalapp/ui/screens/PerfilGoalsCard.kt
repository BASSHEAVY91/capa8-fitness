package com.capa8.fitnesspersonalapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** SRP: renders goals checklist only. Uses PerfilColors — no magic hex. */
@Composable
internal fun PerfilGoalsCard(
    goals: List<String>,
    checked: List<Boolean>,
    onToggle: (Int, Boolean) -> Unit,
    onRemove: (Int) -> Unit,
    onAdd: (String) -> Unit,
) {
    var newGoal by remember { mutableStateOf("") }
    SectionCard(title = "🎯 Objetivos") {
        goals.forEachIndexed { idx, goal ->
            GoalRow(goal, checked.getOrElse(idx) { false }, { onToggle(idx, it) }, { onRemove(idx) })
        }
        Spacer(Modifier.height(8.dp))
        AddGoalRow(newGoal, { newGoal = it }) {
            val trimmed = newGoal.trim()
            if (trimmed.isNotBlank()) { onAdd(trimmed); newGoal = "" }
        }
    }
}

@Composable
private fun GoalRow(text: String, done: Boolean, onToggle: (Boolean) -> Unit, onRemove: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = done, onCheckedChange = onToggle,
            colors = CheckboxDefaults.colors(
                checkedColor = PerfilColors.goalGold,
                uncheckedColor = PerfilColors.border,
                checkmarkColor = Color.White,
            ),
        )
        Text(
            text, modifier = Modifier.weight(1f),
            color = if (done) PerfilColors.textSecondary else PerfilColors.textPrimary,
            fontSize = 15.sp,
            textDecoration = if (done) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
        )
        IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Filled.Close, null, tint = PerfilColors.deleteRed, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun AddGoalRow(value: String, onChange: (String) -> Unit, onAdd: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value, onChange,
            label = { Text("Nuevo objetivo", fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
        )
        Spacer(Modifier.width(8.dp))
        FilledIconButton(onClick = onAdd, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Filled.Add, "Agregar objetivo")
        }
    }
}

/** Reusable card wrapper — DRY: replaces copy-pasted Card boilerplate. */
@Composable
internal fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = PerfilColors.textPrimary)
            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = PerfilColors.surface)
            content()
        }
    }
}
