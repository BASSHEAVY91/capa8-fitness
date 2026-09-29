package com.capa8.fitnesspersonalapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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

/** SRP: renders weekly schedule only. Uses PerfilColors — no magic hex. */
@Composable
internal fun PerfilScheduleCard(
    schedule: List<SchedEntry>,
    onRemove: (Int) -> Unit,
    onAdd: (SchedEntry) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    SectionCard(title = "📅 Horario Semanal") {
        schedule.forEachIndexed { idx, entry ->
            ScheduleEntryRow(entry) { onRemove(idx) }
            if (idx < schedule.lastIndex) HorizontalDivider(color = PerfilColors.surface, modifier = Modifier.padding(vertical = 4.dp))
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
            Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Añadir entrada", fontSize = 13.sp)
        }
    }
    if (showDialog) AddScheduleDialog(onDismiss = { showDialog = false }, onConfirm = { onAdd(it); showDialog = false })
}

@Composable
private fun ScheduleEntryRow(entry: SchedEntry, onRemove: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).background(schedColor(entry.colorHex), CircleShape))
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(entry.activity, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = PerfilColors.textPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                entry.days.forEach { day ->
                    Text(day, fontSize = 10.sp, color = schedColor(entry.colorHex),
                        modifier = Modifier.background(schedColor(entry.colorHex).copy(.1f), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 1.dp))
                }
            }
        }
        IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Filled.Delete, null, tint = PerfilColors.deleteRed, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun AddScheduleDialog(onDismiss: () -> Unit, onConfirm: (SchedEntry) -> Unit) {
    var activity  by remember { mutableStateOf("") }
    var selDays   by remember { mutableStateOf(setOf<String>()) }
    var colorIdx  by remember { mutableStateOf(0) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva actividad") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(activity, { activity = it }, label = { Text("Actividad") },
                    singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                Text("Días", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = PerfilColors.textMuted)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ALL_DAYS.forEach { d ->
                        val sel = d in selDays
                        FilterChip(sel, { selDays = if (sel) selDays - d else selDays + d }, { Text(d, fontSize = 10.sp) })
                    }
                }
                Text("Color", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = PerfilColors.textMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SCHED_COLORS.forEachIndexed { i, hex ->
                        val selected = colorIdx == i
                        Box(Modifier.size(if (selected) 26.dp else 22.dp)
                            .background(schedColor(hex), CircleShape)
                            .then(if (selected) Modifier.background(Color.White.copy(.4f), CircleShape) else Modifier),
                            contentAlignment = Alignment.Center) {
                            Box(Modifier.size(if (selected) 26.dp else 22.dp).background(schedColor(hex), CircleShape)
                                .let { m -> if (!selected) m else m }) {
                                IconButton(onClick = { colorIdx = i }, modifier = Modifier.fillMaxSize()) {}
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (activity.isNotBlank() && selDays.isNotEmpty())
                    onConfirm(SchedEntry(selDays.toList(), activity.trim(), SCHED_COLORS[colorIdx]))
            }) { Text("Añadir") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}
