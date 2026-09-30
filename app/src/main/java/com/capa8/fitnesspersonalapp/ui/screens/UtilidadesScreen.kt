package com.capa8.fitnesspersonalapp.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capa8.fitnesspersonalapp.ui.theme.ContentTextColor
import com.capa8.fitnesspersonalapp.ui.theme.FitnessBlue
import com.capa8.fitnesspersonalapp.ui.theme.FitnessBlueDark
import com.capa8.fitnesspersonalapp.ui.theme.FitnessPersonalAppTheme
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

// ── SharedPreferences keys ─────────────────────────────────────────────────────

private const val PREFS_NAME   = "fitpoli_utilidades"
private const val KEY_BITACORA = "bitacora_entries"
private const val KEY_PESO     = "peso"
private const val KEY_ALTURA   = "altura"
private const val KEY_WEEKLY   = "weekly_records"
private const val KEY_CRONO_S  = "cronometro_seconds"
private const val KEY_EXP_BIT  = "exp_bitacora"
private const val KEY_EXP_CRO  = "exp_cronometro"
private const val KEY_EXP_IMC  = "exp_imc"
private const val KEY_EXP_PRO  = "exp_progreso"
private const val KEY_EXP_NUT  = "exp_nutricion"
private const val KEY_WATER_ML = "water_ml"
private const val KEY_MEALS    = "meals"
private const val LIST_SEP     = "\n~~\n"
private const val REC_SEP      = "|||"

// ── Domain helpers ─────────────────────────────────────────────────────────────

private data class ImcCategory(val label: String, val color: Color)

private fun calcImc(w: Float, h: Float): Float {
    if (h <= 0f || w <= 0f) return 0f
    val hm = h / 100f
    return w / (hm * hm)
}

private fun imcCategory(v: Float): ImcCategory = when {
    v <= 0f  -> ImcCategory("–", Color.Gray)
    v < 18.5f -> ImcCategory("Bajo peso", Color(0xFFFFA726))
    v < 25f  -> ImcCategory("Normal", Color(0xFF4CAF50))
    v < 30f  -> ImcCategory("Sobrepeso", Color(0xFFFF7043))
    else     -> ImcCategory("Obesidad", Color(0xFFF44336))
}

private fun formatTime(s: Int) = "%02d:%02d:%02d".format(s / 3600, (s % 3600) / 60, s % 60)

private data class WeekRecord(val label: String, val kg: Float)

private data class NutritionTargets(
    val calories: Int,
    val proteinG: Int,
    val carbsG: Int,
    val fatG: Int,
    val waterMl: Int
)

private enum class ActivityLevel(val label: String, val factor: Float) {
    Sedentario("Sedentario", 1.2f),
    Moderado("Moderado", 1.55f),
    Alto("Alto", 1.725f)
}

private enum class GoalType(val label: String, val kcalDelta: Int) {
    PerderGrasa("Perder grasa", -300),
    Mantener("Mantener", 0),
    GanarMusculo("Ganar músculo", 250)
}

private fun computeTargets(weightKg: Float, heightCm: Float, activity: ActivityLevel, goal: GoalType): NutritionTargets {
    // Mifflin-St Jeor simplificado (sin edad/sexo) -> aproximación razonable para MVP
    val bmr = 10f * weightKg + 6.25f * heightCm - 5f * 25f + 5f // asumiendo 25 años
    val tdee = (bmr * activity.factor).roundToInt() + goal.kcalDelta

    val protein = (weightKg * 1.8f).roundToInt().coerceAtLeast(80)
    val fat     = (weightKg * 0.9f).roundToInt().coerceAtLeast(50)
    val caloriesFromPF = protein * 4 + fat * 9
    val carbs   = ((tdee - caloriesFromPF) / 4f).roundToInt().coerceAtLeast(80)

    val water = (weightKg * 35f).roundToInt().coerceIn(1800, 3500)
    return NutritionTargets(tdee.coerceAtLeast(1200), protein, carbs, fat, water)
}

private fun mealLabel(type: String) = when (type) {
    "DES" -> "Desayuno"
    "ALM" -> "Almuerzo"
    "CEN" -> "Cena"
    "SNK" -> "Snack"
    else  -> "Comida"
}

private fun encodeMeal(date: String, type: String, text: String) = "$date$REC_SEP$type$REC_SEP${text.replace("\n", " ")}"
private fun decodeMeal(s: String): Triple<String, String, String> {
    val p = s.split(REC_SEP)
    val d = p.getOrNull(0) ?: ""
    val t = p.getOrNull(1) ?: "DES"
    val x = p.drop(2).joinToString(REC_SEP)
    return Triple(d, t, x)
}

private fun encodeWeekly(list: List<WeekRecord>): String =
    list.joinToString(LIST_SEP) { "${it.label}$REC_SEP${it.kg}" }

private fun decodeWeekly(s: String): List<WeekRecord> {
    if (s.isBlank()) return emptyList()
    return s.split(LIST_SEP).mapNotNull { entry ->
        val p = entry.split(REC_SEP)
        if (p.size >= 2) WeekRecord(p[0], p[1].toFloatOrNull() ?: 0f) else null
    }
}

// Bitácora entry encoding
private const val SEP = "|||"
private fun encodeBitacora(emoji: String, name: String, minutes: String, date: String) =
    "$emoji$SEP$name$SEP$minutes$SEP$date"
private fun decodeBitacora(s: String): List<String> {
    val parts = s.split(SEP)
    return if (parts.size >= 4) parts else listOf("\uD83D\uDCAA", s, "0", "")
}
private val EMOJIS = listOf("\uD83D\uDCAA", "\uD83D\uDD25", "\uD83D\uDE0A", "\uD83D\uDE13", "\uD83D\uDE34")
private val EMOJI_LABELS = listOf("Fuerza", "Intenso", "Bien", "Cansado", "Agotado")

// ── Collapsible card header ────────────────────────────────────────────────────

@Composable
private fun CollapsibleHeader(
    icon: ImageVector,
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = FitnessBlue, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(10.dp))
        Text(title, fontWeight = FontWeight.Bold, color = FitnessBlue, fontSize = 17.sp, modifier = Modifier.weight(1f))
        Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = if (expanded) "Colapsar" else "Expandir",
            tint = FitnessBlue,
            modifier = Modifier.size(24.dp)
        )
    }
}

// ── Main screen ────────────────────────────────────────────────────────────────

@Composable
fun UtilidadesScreen(modifier: Modifier = Modifier) {

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp

    val horizontalPadding  = if (screenWidthDp < 370) 6.dp  else 16.dp
    val verticalPadding    = if (screenWidthDp < 370) 8.dp  else 12.dp
    val timeFontSize       = if (screenWidthDp < 370) 34.sp else 50.sp
    val circleSize         = if (screenWidthDp < 370) 50.dp else 68.dp
    val iconSize           = if (screenWidthDp < 370) 24.dp else 34.dp
    val buttonsSpace       = if (screenWidthDp < 370) 8.dp  else 20.dp
    val emojiFontSize      = if (screenWidthDp < 370) 15.sp else 22.sp
    val emojiPaddingV      = if (screenWidthDp < 370) 4.dp  else 8.dp
    val cardInnerHPadding  = if (screenWidthDp < 370) 10.dp else 20.dp
    val emojiLabelFontSize = if (screenWidthDp < 340) 7.sp  else if (screenWidthDp < 370) 8.sp else 9.sp
    val chipHPadding       = if (screenWidthDp < 370) 5.dp  else 10.dp
    val chipFontSize       = if (screenWidthDp < 370) 10.sp else 11.sp
    val weeklyStackRows    = screenWidthDp < 370

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    // ── Section expanded states (persisted) ───────────────────────────────────
    var bitacoraExpanded  by remember { mutableStateOf(prefs.getBoolean(KEY_EXP_BIT, false)) }
    var cronoExpanded     by remember { mutableStateOf(prefs.getBoolean(KEY_EXP_CRO, false)) }
    var imcExpanded       by remember { mutableStateOf(prefs.getBoolean(KEY_EXP_IMC, false)) }
    var progresoExpanded  by remember { mutableStateOf(prefs.getBoolean(KEY_EXP_PRO, false)) }
    var nutricionExpanded by remember { mutableStateOf(prefs.getBoolean(KEY_EXP_NUT, false)) }

    LaunchedEffect(bitacoraExpanded)  { prefs.edit().putBoolean(KEY_EXP_BIT, bitacoraExpanded).apply() }
    LaunchedEffect(cronoExpanded)     { prefs.edit().putBoolean(KEY_EXP_CRO, cronoExpanded).apply() }
    LaunchedEffect(imcExpanded)       { prefs.edit().putBoolean(KEY_EXP_IMC, imcExpanded).apply() }
    LaunchedEffect(progresoExpanded)  { prefs.edit().putBoolean(KEY_EXP_PRO, progresoExpanded).apply() }
    LaunchedEffect(nutricionExpanded) { prefs.edit().putBoolean(KEY_EXP_NUT, nutricionExpanded).apply() }

    // ── Bitácora state (persisted) ────────────────────────────────────────────
    var bitacoraEntries by remember {
        mutableStateOf(
            prefs.getString(KEY_BITACORA, "")?.let {
                if (it.isBlank()) emptyList() else it.split(LIST_SEP)
            } ?: emptyList()
        )
    }
    var bitacoraName    by remember { mutableStateOf("") }
    var bitacoraMinutes by remember { mutableStateOf("") }
    var bitacoraEmoji   by remember { mutableStateOf(EMOJIS[0]) }

    LaunchedEffect(bitacoraEntries) {
        prefs.edit().putString(KEY_BITACORA, bitacoraEntries.joinToString(LIST_SEP)).apply()
    }

    // ── Cronómetro state (persisted) ─────────────────────────────────────────
    var totalSeconds by remember { mutableIntStateOf(prefs.getInt(KEY_CRONO_S, 0)) }
    var isRunning    by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) { while (isRunning) { delay(1.seconds); totalSeconds++ } }
    LaunchedEffect(totalSeconds) { if (!isRunning) prefs.edit().putInt(KEY_CRONO_S, totalSeconds).apply() }

    // ── IMC state (persisted) ─────────────────────────────────────────────────
    var pesoText   by remember { mutableStateOf(prefs.getString(KEY_PESO,   "72")  ?: "72") }
    var alturaText by remember { mutableStateOf(prefs.getString(KEY_ALTURA, "175") ?: "175") }
    val imc = remember(pesoText, alturaText) {
        calcImc(pesoText.toFloatOrNull() ?: 0f, alturaText.toFloatOrNull() ?: 0f)
    }
    val cat = imcCategory(imc)

    LaunchedEffect(pesoText)   { prefs.edit().putString(KEY_PESO,   pesoText).apply() }
    LaunchedEffect(alturaText) { prefs.edit().putString(KEY_ALTURA, alturaText).apply() }

    // ── Progreso state (persisted) ────────────────────────────────────────────
    val defaultWeekly = listOf(
        WeekRecord("Semana 1", 68f), WeekRecord("Semana 2", 70f),
        WeekRecord("Semana 3", 71.5f), WeekRecord("Semana 4", 72f)
    )
    var weeklyRecords by remember {
        mutableStateOf(
            decodeWeekly(prefs.getString(KEY_WEEKLY, "") ?: "").ifEmpty { defaultWeekly }
        )
    }
    var newWeekLabel  by remember { mutableStateOf("") }
    var newWeekWeight by remember { mutableStateOf("") }

    LaunchedEffect(weeklyRecords) {
        prefs.edit().putString(KEY_WEEKLY, encodeWeekly(weeklyRecords)).apply()
    }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val dayFormatter  = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val todayKey      = remember { dayFormatter.format(Date()) }

    // ── Nutrición state (persisted) ───────────────────────────────────────────
    var activityLevel by remember { mutableStateOf(ActivityLevel.valueOf(prefs.getString("act_level", ActivityLevel.Moderado.name) ?: ActivityLevel.Moderado.name)) }
    var goalType      by remember { mutableStateOf(GoalType.valueOf(prefs.getString("goal_type", GoalType.PerderGrasa.name) ?: GoalType.PerderGrasa.name)) }
    var waterMl       by remember { mutableIntStateOf(prefs.getInt(KEY_WATER_ML, 0)) }
    var mealType      by remember { mutableStateOf("DES") }
    var mealText      by remember { mutableStateOf("") }
    var meals         by remember {
        mutableStateOf(
            prefs.getString(KEY_MEALS, "")?.let { raw -> if (raw.isBlank()) emptyList() else raw.split(LIST_SEP) } ?: emptyList()
        )
    }

    LaunchedEffect(activityLevel) { prefs.edit().putString("act_level", activityLevel.name).apply() }
    LaunchedEffect(goalType)      { prefs.edit().putString("goal_type", goalType.name).apply() }
    LaunchedEffect(waterMl)       { prefs.edit().putInt(KEY_WATER_ML, waterMl).apply() }
    LaunchedEffect(meals)         { prefs.edit().putString(KEY_MEALS, meals.joinToString(LIST_SEP)).apply() }

    val profileWeight = pesoText.toFloatOrNull() ?: 70f
    val profileHeight = alturaText.toFloatOrNull() ?: 170f
    val targets = remember(profileWeight, profileHeight, activityLevel, goalType) {
        computeTargets(profileWeight, profileHeight, activityLevel, goalType)
    }

    val lastTrainingToday = remember(bitacoraEntries, todayKey) {
        bitacoraEntries.firstOrNull { encoded ->
            val p = decodeBitacora(encoded)
            p.getOrNull(3)?.startsWith(todayKey) == true
        }
    }

    // ── Root layout ───────────────────────────────────────────────────────────
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // ── 0. BITACORA ───────────────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = cardInnerHPadding, vertical = 16.dp)) {
                CollapsibleHeader(Icons.Filled.Book, "Bit\u00e1cora de Entrenamiento", bitacoraExpanded) {
                    bitacoraExpanded = !bitacoraExpanded
                }
                AnimatedVisibility(visible = bitacoraExpanded, enter = expandVertically(), exit = shrinkVertically()) {
                    Column {
                        Spacer(Modifier.height(14.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = bitacoraName, onValueChange = { bitacoraName = it },
                                label = { Text("Ejercicio / Sesi\u00f3n", fontSize = 12.sp) },
                                singleLine = true, modifier = Modifier.weight(1.6f),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FitnessBlue, unfocusedBorderColor = Color(0xFFBDBDBD))
                            )
                            OutlinedTextField(
                                value = bitacoraMinutes, onValueChange = { bitacoraMinutes = it },
                                label = { Text("Min", fontSize = 12.sp) }, singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(0.9f), shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FitnessBlue, unfocusedBorderColor = Color(0xFFBDBDBD))
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                        Text("\u00bfC\u00f3mo te sientes?", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = ContentTextColor)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            EMOJIS.forEachIndexed { idx, emoji ->
                                val selected = emoji == bitacoraEmoji
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { bitacoraEmoji = emoji }
                                        .background(if (selected) FitnessBlue.copy(alpha = 0.13f) else Color.Transparent, RoundedCornerShape(10.dp))
                                        .border(if (selected) 2.dp else 1.dp, if (selected) FitnessBlue else Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
                                        .padding(vertical = emojiPaddingV)
                                ) {
                                    Text(emoji, fontSize = emojiFontSize)
                                    Spacer(Modifier.height(3.dp))
                                    if (screenWidthDp >= 320) {
                                        Text(
                                            EMOJI_LABELS[idx], fontSize = emojiLabelFontSize,
                                            maxLines = 1,
                                            color = if (selected) FitnessBlue else ContentTextColor,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (bitacoraName.isNotBlank()) {
                                    val now  = dateFormatter.format(Date())
                                    val mins = if (bitacoraMinutes.isBlank()) "0" else bitacoraMinutes
                                    bitacoraEntries = listOf(encodeBitacora(bitacoraEmoji, bitacoraName.trim(), mins, now)) + bitacoraEntries
                                    bitacoraName = ""; bitacoraMinutes = ""; bitacoraEmoji = EMOJIS[0]
                                }
                            },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FitnessBlue, contentColor = Color.White)
                        ) {
                            Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Registrar sesi\u00f3n", fontWeight = FontWeight.SemiBold)
                        }
                        if (bitacoraEntries.isNotEmpty()) {
                            Spacer(Modifier.height(16.dp))
                            Text("Historial reciente", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = FitnessBlueDark)
                            Spacer(Modifier.height(10.dp))
                            bitacoraEntries.forEachIndexed { idx, encoded ->
                                val parts = decodeBitacora(encoded)
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(46.dp).background(FitnessBlue.copy(alpha = 0.10f), CircleShape), contentAlignment = Alignment.Center) {
                                        Text(parts[0], fontSize = 22.sp)
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(parts[1], fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = FitnessBlueDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("${parts[2]} min  \u00b7  ${parts[3]}", fontSize = 12.sp, color = ContentTextColor)
                                    }
                                    IconButton(onClick = { bitacoraEntries = bitacoraEntries.toMutableList().also { it.removeAt(idx) } }, modifier = Modifier.size(32.dp)) {
                                        Icon(Icons.Filled.Close, "Eliminar", tint = Color(0xFFF44336), modifier = Modifier.size(18.dp))
                                    }
                                }
                                if (idx < bitacoraEntries.lastIndex) Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF0F0F0)))
                            }
                        }
                    }
                }
            }
        }

        // ── 0.5 NUTRICIÓN ─────────────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = cardInnerHPadding, vertical = 16.dp)) {
                CollapsibleHeader(Icons.Filled.RestaurantMenu, "Nutrición", nutricionExpanded) {
                    nutricionExpanded = !nutricionExpanded
                }
                AnimatedVisibility(visible = nutricionExpanded, enter = expandVertically(), exit = shrinkVertically()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(Modifier.height(14.dp))

                        // ── Targets ───────────────────────────────────────
                        Text("Objetivo diario", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = FitnessBlueDark)
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(FitnessBlue.copy(alpha = 0.10f), RoundedCornerShape(10.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Text("${targets.calories} kcal", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = FitnessBlueDark)
                                Spacer(Modifier.height(6.dp))
                                Text("Proteína: ${targets.proteinG} g  ·  Carbs: ${targets.carbsG} g  ·  Grasas: ${targets.fatG} g", fontSize = 12.sp, color = ContentTextColor)
                                Spacer(Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    ActivityLevel.entries.forEach { lvl ->
                                        val selected = lvl == activityLevel
                                        val shortLabel = when (lvl) {
                                            ActivityLevel.Sedentario -> if (screenWidthDp < 370) "Sed." else lvl.label
                                            ActivityLevel.Moderado   -> if (screenWidthDp < 370) "Mod." else lvl.label
                                            ActivityLevel.Alto       -> lvl.label
                                        }
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(if (selected) FitnessBlue else Color.Transparent, RoundedCornerShape(8.dp))
                                                .border(1.dp, if (selected) FitnessBlue else Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                                                .clickable { activityLevel = lvl }
                                                .padding(horizontal = chipHPadding, vertical = 6.dp)
                                        ) {
                                            Text(
                                                shortLabel,
                                                fontSize = chipFontSize,
                                                color = if (selected) Color.White else ContentTextColor,
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                                Spacer(Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    GoalType.entries.forEach { g ->
                                        val selected = g == goalType
                                        val shortLabel = when (g) {
                                            GoalType.PerderGrasa  -> if (screenWidthDp < 370) "- Grasa" else g.label
                                            GoalType.Mantener     -> g.label
                                            GoalType.GanarMusculo -> if (screenWidthDp < 370) "+ Músculo" else g.label
                                        }
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(if (selected) FitnessBlueDark else Color.Transparent, RoundedCornerShape(8.dp))
                                                .border(1.dp, if (selected) FitnessBlueDark else Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                                                .clickable { goalType = g }
                                                .padding(horizontal = chipHPadding, vertical = 6.dp)
                                        ) {
                                            Text(
                                                shortLabel,
                                                fontSize = chipFontSize,
                                                color = if (selected) Color.White else ContentTextColor,
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // ── Water ─────────────────────────────────────────
                        Spacer(Modifier.height(14.dp))
                        Text("Agua", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = FitnessBlueDark)
                        Spacer(Modifier.height(8.dp))
                        val waterProgress = (waterMl / targets.waterMl.toFloat()).coerceIn(0f, 1f)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${waterMl} / ${targets.waterMl} ml", fontSize = 12.sp, color = ContentTextColor, modifier = Modifier.weight(1f))
                            Button(
                                onClick = { waterMl = (waterMl + 250).coerceAtMost(targets.waterMl + 1000) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FitnessBlue)
                            ) { Text("+250", fontWeight = FontWeight.SemiBold) }
                            Spacer(Modifier.width(8.dp))
                            IconButton(onClick = { waterMl = 0 }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Filled.Refresh, "Reset agua", tint = FitnessBlueDark, modifier = Modifier.size(18.dp))
                            }
                        }
                        LinearProgressIndicator(
                            progress = { waterProgress },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = FitnessBlue,
                            trackColor = FitnessBlue.copy(alpha = 0.18f),
                            strokeCap = StrokeCap.Round
                        )

                        // ── Meals ─────────────────────────────────────────
                        Spacer(Modifier.height(16.dp))
                        Text("Comidas rápidas", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = FitnessBlueDark)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("DES" to "Des", "ALM" to "Alm", "CEN" to "Cen", "SNK" to "Snk").forEach { (code, short) ->
                                val selected = code == mealType
                                Box(
                                    modifier = Modifier
                                        .background(if (selected) FitnessBlue.copy(alpha = 0.15f) else Color.Transparent, RoundedCornerShape(8.dp))
                                        .border(1.dp, if (selected) FitnessBlue else Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                                        .clickable { mealType = code }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) { Text(short, fontSize = 11.sp, color = if (selected) FitnessBlueDark else ContentTextColor, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = mealText,
                            onValueChange = { mealText = it },
                            label = { Text("¿Qué comiste?", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FitnessBlue,
                                unfocusedBorderColor = Color(0xFFBDBDBD)
                            )
                        )
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (mealText.isNotBlank()) {
                                    meals = listOf(encodeMeal(todayKey, mealType, mealText.trim())) + meals
                                    mealText = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FitnessBlueDark, contentColor = Color.White)
                        ) {
                            Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Agregar comida", fontWeight = FontWeight.SemiBold)
                        }

                        val todayMeals = meals.map { decodeMeal(it) }.filter { it.first == todayKey }
                        if (todayMeals.isNotEmpty()) {
                            Spacer(Modifier.height(14.dp))
                            Text("Hoy", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = FitnessBlueDark)
                            Spacer(Modifier.height(8.dp))
                            todayMeals.forEachIndexed { idx, m ->
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(FitnessBlue.copy(alpha = 0.10f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(mealLabel(m.second), fontSize = 11.sp, color = FitnessBlueDark, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Text(m.third, fontSize = 12.sp, color = ContentTextColor, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    IconButton(
                                        onClick = {
                                            // remove the idx-th meal of today from the global list
                                            val positions = meals.withIndex().filter { decodeMeal(it.value).first == todayKey }.map { it.index }
                                            val removeAt = positions.getOrNull(idx)
                                            if (removeAt != null) meals = meals.toMutableList().also { it.removeAt(removeAt) }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Filled.Close, "Eliminar comida", tint = Color(0xFFF44336), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        // ── Recommendation based on training ───────────────
                        if (lastTrainingToday != null) {
                            Spacer(Modifier.height(14.dp))
                            val p = decodeBitacora(lastTrainingToday)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFFF3E0), RoundedCornerShape(10.dp))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Text("Post-entreno sugerido", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                    Spacer(Modifier.height(6.dp))
                                    Text("Hoy registraste: ${p[1]} (${p[2]} min).", fontSize = 12.sp, color = ContentTextColor)
                                    Text("Meta rápida: 25–35 g proteína + 1 porción de carbos (arroz/papa/arepa/fruta) + agua.", fontSize = 12.sp, color = ContentTextColor)
                                }
                            }
                        }

                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }

        // ── 1. CRONOMETRO ─────────────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = cardInnerHPadding, vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                CollapsibleHeader(Icons.Filled.Timer, "Cron\u00f3metro", cronoExpanded) { cronoExpanded = !cronoExpanded }
                AnimatedVisibility(visible = cronoExpanded, enter = expandVertically(), exit = shrinkVertically()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(20.dp))

                        // ── Time display ───────────────────────────────────
                        Text(
                            text = formatTime(totalSeconds),
                            fontSize = timeFontSize,
                            fontWeight = FontWeight.ExtraBold,
                            color = FitnessBlueDark,
                            letterSpacing = 3.sp
                        )

                        Spacer(Modifier.height(28.dp))

                        // ── Circular controls ──────────────────────────────
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Play
                            Box(
                                modifier = Modifier
                                    .size(circleSize)
                                    .background(Color(0xFF4CAF50), CircleShape)
                                    .clickable { if (!isRunning) isRunning = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.PlayArrow, "Iniciar", tint = Color.White, modifier = Modifier.size(iconSize))
                            }
                            Spacer(Modifier.width(buttonsSpace))
                            // Stop
                            Box(
                                modifier = Modifier
                                    .size(circleSize)
                                    .background(Color(0xFFE53935), CircleShape)
                                    .clickable { isRunning = false },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Stop, "Detener", tint = Color.White, modifier = Modifier.size(iconSize))
                            }
                            Spacer(Modifier.width(buttonsSpace))
                            // Reset
                            Box(
                                modifier = Modifier
                                    .size(circleSize)
                                    .background(Color(0xFF7986CB), CircleShape)
                                    .clickable {
                                        isRunning = false
                                        totalSeconds = 0
                                        prefs.edit().putInt(KEY_CRONO_S, 0).apply()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Refresh, "Reiniciar", tint = Color.White, modifier = Modifier.size(iconSize))
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                    }
                }
            }
        }

        // ── 2. IMC ────────────────────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = cardInnerHPadding, vertical = 16.dp)) {
                CollapsibleHeader(Icons.Filled.Calculate, "Calculadora IMC", imcExpanded) { imcExpanded = !imcExpanded }
                AnimatedVisibility(visible = imcExpanded, enter = expandVertically(), exit = shrinkVertically()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(Modifier.height(14.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = pesoText, onValueChange = { pesoText = it },
                                label = { Text("Peso (kg)") }, singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FitnessBlue, unfocusedBorderColor = Color(0xFFBDBDBD))
                            )
                            OutlinedTextField(
                                value = alturaText, onValueChange = { alturaText = it },
                                label = { Text("Altura (cm)") }, singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FitnessBlue, unfocusedBorderColor = Color(0xFFBDBDBD))
                            )
                        }
                        if (imc > 0f) {
                            Spacer(Modifier.height(16.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth().background(cat.color.copy(alpha = 0.10f), RoundedCornerShape(10.dp)).padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("%.1f".format(imc), fontSize = 40.sp, fontWeight = FontWeight.Bold, color = cat.color)
                                    Text(cat.label, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = cat.color)
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            val progress = ((imc - 10f) / 30f).coerceIn(0f, 1f)
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth().height(8.dp),
                                color = cat.color,
                                trackColor = cat.color.copy(alpha = 0.20f),
                                strokeCap = StrokeCap.Round
                            )
                            Spacer(Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("< 18.5\nBajo peso", fontSize = 10.sp, color = Color(0xFFFFA726))
                                Text("18.5–24.9\nNormal",   fontSize = 10.sp, color = Color(0xFF4CAF50))
                                Text("25–29.9\nSobrepeso",  fontSize = 10.sp, color = Color(0xFFFF7043))
                                Text("≥ 30\nObesidad",      fontSize = 10.sp, color = Color(0xFFF44336))
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }

        // ── 3. PROGRESO SEMANAL ───────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = cardInnerHPadding, vertical = 16.dp)) {
                CollapsibleHeader(Icons.AutoMirrored.Filled.TrendingUp, "Progreso Semanal", progresoExpanded) { progresoExpanded = !progresoExpanded }
                AnimatedVisibility(visible = progresoExpanded, enter = expandVertically(), exit = shrinkVertically()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(Modifier.height(14.dp))
                        val maxKg = weeklyRecords.maxOfOrNull { it.kg } ?: 100f
                        weeklyRecords.forEachIndexed { idx, rec ->
                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
                                if (weeklyStackRows) {
                                    // Stacked layout for screens < 370dp
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            rec.label,
                                            fontSize = 12.sp,
                                            color = ContentTextColor,
                                            modifier = Modifier.weight(1f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            "%.1f kg".format(rec.kg),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = FitnessBlueDark
                                        )
                                        IconButton(
                                            onClick = { weeklyRecords = weeklyRecords.toMutableList().also { it.removeAt(idx) } },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Filled.Close, "Eliminar", tint = Color(0xFFF44336), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    LinearProgressIndicator(
                                        progress = { if (maxKg > 0f) rec.kg / maxKg else 0f },
                                        modifier = Modifier.fillMaxWidth().height(7.dp).padding(end = 4.dp),
                                        color = FitnessBlue,
                                        trackColor = FitnessBlue.copy(alpha = 0.18f),
                                        strokeCap = StrokeCap.Round
                                    )
                                } else {
                                    // Single-row layout for wider screens
                                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            rec.label,
                                            fontSize = 13.sp,
                                            color = ContentTextColor,
                                            modifier = Modifier.weight(1.2f)
                                        )
                                        Text(
                                            "%.1f kg".format(rec.kg),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = FitnessBlueDark,
                                            modifier = Modifier.weight(0.8f)
                                        )
                                        LinearProgressIndicator(
                                            progress = { if (maxKg > 0f) rec.kg / maxKg else 0f },
                                            modifier = Modifier.weight(2f).height(8.dp),
                                            color = FitnessBlue,
                                            trackColor = FitnessBlue.copy(alpha = 0.18f),
                                            strokeCap = StrokeCap.Round
                                        )
                                        IconButton(
                                            onClick = { weeklyRecords = weeklyRecords.toMutableList().also { it.removeAt(idx) } },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Filled.Close, "Eliminar", tint = Color(0xFFF44336), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = newWeekLabel, onValueChange = { newWeekLabel = it },
                                label = { Text("Semana / Fecha", fontSize = 12.sp) }, singleLine = true,
                                modifier = Modifier.weight(1.5f), shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FitnessBlue, unfocusedBorderColor = Color(0xFFBDBDBD))
                            )
                            OutlinedTextField(
                                value = newWeekWeight, onValueChange = { newWeekWeight = it },
                                label = { Text("Peso (kg)", fontSize = 12.sp) }, singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FitnessBlue, unfocusedBorderColor = Color(0xFFBDBDBD))
                            )
                            Button(
                                onClick = {
                                    val w = newWeekWeight.toFloatOrNull()
                                    if (newWeekLabel.isNotBlank() && w != null) {
                                        weeklyRecords = weeklyRecords + WeekRecord(newWeekLabel.trim(), w)
                                        newWeekLabel = ""; newWeekWeight = ""
                                    }
                                },
                                modifier = Modifier.align(Alignment.CenterVertically),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FitnessBlue)
                            ) { Icon(Icons.Filled.Add, null) }
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// ── Preview ────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true, device = "spec:width=360dp,height=800dp,dpi=480")
@Composable
private fun UtilidadesScreenPreview() {
    FitnessPersonalAppTheme { UtilidadesScreen() }
}
