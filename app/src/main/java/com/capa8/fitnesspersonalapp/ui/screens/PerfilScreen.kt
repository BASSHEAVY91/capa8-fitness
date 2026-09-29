package com.capa8.fitnesspersonalapp.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capa8.fitnesspersonalapp.ui.theme.FitnessBlue
import com.capa8.fitnesspersonalapp.ui.theme.FitnessBlueDark
import com.capa8.fitnesspersonalapp.ui.theme.FitnessPersonalAppTheme

// ── SharedPreferences keys ────────────────────────────────────────────────────

private const val PERFIL_PREFS    = "fitpoli_perfil"
private const val P_NAME          = "name"
private const val P_EMAIL         = "email"
private const val P_WEIGHT        = "weight"
private const val P_HEIGHT        = "height"
private const val P_FAT           = "fat"
private const val P_NOTES         = "notes"
private const val P_GOALS         = "goals"
private const val P_CHECKED       = "checked"
private const val GOAL_SEP        = "\n~~\n"

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun calcImc(w: String, h: String): String {
    val wf = w.toFloatOrNull() ?: return "-"
    val hm = (h.toFloatOrNull() ?: return "-") / 100f
    return if (hm > 0f) "%.1f".format(wf / (hm * hm)) else "-"
}

private fun encodeGoals(list: List<String>): String = list.joinToString(GOAL_SEP)
private fun decodeGoals(s: String): List<String> =
    if (s.isBlank()) emptyList() else s.split(GOAL_SEP)

private fun encodeChecked(list: List<Boolean>): String = list.joinToString(",") { if (it) "1" else "0" }
private fun decodeChecked(s: String): List<Boolean> =
    if (s.isBlank()) emptyList() else s.split(",").map { it == "1" }

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun PerfilScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val prefs   = remember { context.getSharedPreferences(PERFIL_PREFS, Context.MODE_PRIVATE) }

    // ── Persisted profile fields ──────────────────────────────────────────────
    var name   by remember { mutableStateOf(prefs.getString(P_NAME,   "Juan Perez")           ?: "Juan Perez") }
    var email  by remember { mutableStateOf(prefs.getString(P_EMAIL,  "juan@fitpoli.com")      ?: "juan@fitpoli.com") }
    var weight by remember { mutableStateOf(prefs.getString(P_WEIGHT, "72")                   ?: "72") }
    var height by remember { mutableStateOf(prefs.getString(P_HEIGHT, "175")                  ?: "175") }
    var fat    by remember { mutableStateOf(prefs.getString(P_FAT,    "12")                   ?: "12") }
    var notes  by remember { mutableStateOf(prefs.getString(P_NOTES,  "Mantener hidratacion. Dormir 8 horas.") ?: "Mantener hidratacion. Dormir 8 horas.") }

    val defaultGoals = listOf("Perder 5 kg en 3 meses", "Correr 5 km sin parar", "Aumentar fuerza en piernas")
    var goals by remember {
        mutableStateOf(
            decodeGoals(prefs.getString(P_GOALS, "") ?: "").ifEmpty { defaultGoals }
        )
    }
    var checked by remember {
        mutableStateOf(
            decodeChecked(prefs.getString(P_CHECKED, "") ?: "").let { saved ->
                if (saved.size == goals.size) saved else List(goals.size) { false }
            }
        )
    }
    if (checked.size != goals.size) checked = List(goals.size) { i -> checked.getOrElse(i) { false } }

    // ── Edit-mode temporaries ─────────────────────────────────────────────────
    var editMode  by remember { mutableStateOf(false) }
    var tmpName   by remember { mutableStateOf(name) }
    var tmpEmail  by remember { mutableStateOf(email) }
    var tmpWeight by remember { mutableStateOf(weight) }
    var tmpHeight by remember { mutableStateOf(height) }
    var tmpFat    by remember { mutableStateOf(fat) }
    var tmpNotes  by remember { mutableStateOf(notes) }
    var newGoal   by remember { mutableStateOf("") }

    // ── Persist on every change ───────────────────────────────────────────────
    LaunchedEffect(name, email, weight, height, fat, notes) {
        prefs.edit()
            .putString(P_NAME, name).putString(P_EMAIL, email)
            .putString(P_WEIGHT, weight).putString(P_HEIGHT, height)
            .putString(P_FAT, fat).putString(P_NOTES, notes)
            .apply()
    }
    LaunchedEffect(goals) { prefs.edit().putString(P_GOALS, encodeGoals(goals)).apply() }
    LaunchedEffect(checked) { prefs.edit().putString(P_CHECKED, encodeChecked(checked)).apply() }

    // ── Text-field color scheme for the blue header ───────────────────────────
    val whiteColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor        = Color.White, unfocusedTextColor = Color.White,
        focusedBorderColor      = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
        focusedLabelColor       = Color.White.copy(0.9f), unfocusedLabelColor = Color.White.copy(0.6f),
        cursorColor             = Color.White
    )

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ── Header card ───────────────────────────────────────────────────────
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(4.dp)) {
            Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(FitnessBlueDark, FitnessBlue)))) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                    // Edit / Save / Cancel buttons
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        if (!editMode) {
                            IconButton(onClick = {
                                tmpName = name; tmpEmail = email; tmpWeight = weight
                                tmpHeight = height; tmpFat = fat; tmpNotes = notes
                                editMode = true
                            }) {
                                Icon(Icons.Filled.Edit, "Editar", tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                        } else {
                            IconButton(onClick = {
                                name = tmpName.trim().ifBlank { name }
                                email = tmpEmail.trim().ifBlank { email }
                                weight = tmpWeight.trim().ifBlank { weight }
                                height = tmpHeight.trim().ifBlank { height }
                                fat = tmpFat.trim().ifBlank { fat }
                                notes = tmpNotes
                                editMode = false
                            }) {
                                Icon(Icons.Filled.Check, "Guardar", tint = Color(0xFF76FF03), modifier = Modifier.size(22.dp))
                            }
                            IconButton(onClick = { editMode = false }) {
                                Icon(Icons.Filled.Close, "Cancelar", tint = Color.White.copy(0.7f), modifier = Modifier.size(22.dp))
                            }
                        }
                    }

                    // Avatar
                    Box(
                        Modifier.size(88.dp).clip(CircleShape)
                            .background(Color.White.copy(0.15f))
                            .border(3.dp, Color.White.copy(0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Person, null, tint = Color.White, modifier = Modifier.size(60.dp))
                    }
                    Spacer(Modifier.height(12.dp))

                    // Name & email
                    if (!editMode) {
                        Text(name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(email, fontSize = 14.sp, color = Color.White.copy(0.8f))
                    } else {
                        OutlinedTextField(
                            value = tmpName, onValueChange = { tmpName = it },
                            label = { Text("Nombre") }, singleLine = true,
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = whiteColors
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = tmpEmail, onValueChange = { tmpEmail = it },
                            label = { Text("Correo") }, singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = whiteColors
                        )
                    }
                    Spacer(Modifier.height(18.dp))

                    // Stats row
                    if (!editMode) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatChip("$weight kg", "Peso",   Modifier.weight(1f))
                            StatChip("$height cm", "Altura", Modifier.weight(1f))
                            StatChip(calcImc(weight, height), "IMC", Modifier.weight(1f))
                            StatChip("$fat%",      "Grasa",  Modifier.weight(1f))
                        }
                    } else {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            EditStatField(tmpWeight, { tmpWeight = it }, "Peso kg",  Modifier.weight(1f), whiteColors)
                            EditStatField(tmpHeight, { tmpHeight = it }, "Alt cm",   Modifier.weight(1f), whiteColors)
                            EditStatField(tmpFat,    { tmpFat    = it }, "Grasa %",  Modifier.weight(1f), whiteColors)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        // ── Goals ─────────────────────────────────────────────────────────────
        ProfileCard(Icons.Filled.EmojiEvents, "Metas de Entrenamiento", Color(0xFFFFA726)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                goals.forEachIndexed { i, goal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .then(if (!editMode) Modifier.clickable {
                                checked = checked.mapIndexed { j, v -> if (j == i) !v else v }
                            } else Modifier)
                            .background(
                                if (checked.getOrElse(i) { false }) Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!editMode) {
                            Icon(
                                if (checked.getOrElse(i) { false }) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                null,
                                tint = if (checked.getOrElse(i) { false }) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                        }
                        Text(
                            goal, fontSize = 14.sp, modifier = Modifier.weight(1f),
                            color = if (checked.getOrElse(i) { false }) Color(0xFF2E7D32) else Color(0xFF424242)
                        )
                        if (editMode) {
                            IconButton(
                                onClick = {
                                    goals   = goals.toMutableList().also { it.removeAt(i) }
                                    checked = checked.toMutableList().also { if (i < it.size) it.removeAt(i) }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Filled.Close, "Eliminar", tint = Color(0xFFF44336), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
                // Add new goal (edit mode only)
                if (editMode) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newGoal, onValueChange = { newGoal = it },
                            label = { Text("Nueva meta") }, singleLine = true,
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FitnessBlue, unfocusedBorderColor = Color(0xFFBDBDBD)
                            )
                        )
                        IconButton(onClick = {
                            if (newGoal.isNotBlank()) {
                                goals   = goals + newGoal.trim()
                                checked = checked + false
                                newGoal = ""
                            }
                        }) {
                            Icon(Icons.Filled.Add, "Agregar meta", tint = FitnessBlue, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
        }

        // ── Schedule ──────────────────────────────────────────────────────────
        ProfileCard(Icons.Filled.Schedule, "Horario de Entrenamiento", Color(0xFF42A5F5)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(
                    Triple(listOf("LUN", "MIE", "VIE"), "Fuerza",      Color(0xFF1565C0)),
                    Triple(listOf("MAR", "JUE"),         "Cardio HIIT", Color(0xFFE53935))
                ).forEach { (days, activity, color) ->
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                            .background(color.copy(0.08f)).padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            days.forEach { day ->
                                Box(
                                    Modifier.clip(RoundedCornerShape(4.dp)).background(color)
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(day, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(activity, fontSize = 14.sp, color = Color(0xFF424242), fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // ── Notes ─────────────────────────────────────────────────────────────
        ProfileCard(Icons.Filled.EditNote, "Notas Personales", Color(0xFF66BB6A)) {
            if (!editMode) {
                Text(notes, fontSize = 14.sp, color = Color(0xFF424242), lineHeight = 20.sp)
            } else {
                OutlinedTextField(
                    value = tmpNotes, onValueChange = { tmpNotes = it },
                    label = { Text("Notas") }, minLines = 3,
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FitnessBlue, unfocusedBorderColor = Color(0xFFBDBDBD)
                    )
                )
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

// ── Sub-composables ───────────────────────────────────────────────────────────

@Composable
private fun StatChip(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(10.dp)).background(Color.White.copy(0.15f))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
        Text(label,  fontSize = 10.sp, color = Color.White.copy(0.8f), textAlign = TextAlign.Center)
    }
}

@Composable
private fun EditStatField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label, fontSize = 10.sp) }, singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier, shape = RoundedCornerShape(8.dp), colors = colors
    )
}

@Composable
private fun ProfileCard(
    icon: ImageVector,
    title: String,
    iconTint: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF212121))
            }
            content()
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true, device = "spec:width=360dp,height=800dp,dpi=480")
@Composable
fun PerfilScreenPreview() {
    FitnessPersonalAppTheme { PerfilScreen() }
}
