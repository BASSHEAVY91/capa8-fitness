package com.capa8.fitnesspersonalapp.ui.screens

import androidx.compose.ui.graphics.Color

// ── SharedPreferences keys ────────────────────────────────────────────────────
const val PERFIL_PREFS = "fitpoli_perfil"
const val P_NAME       = "name"
const val P_EMAIL      = "email"
const val P_WEIGHT     = "weight"
const val P_HEIGHT     = "height"
const val P_FAT        = "fat"
const val P_NOTES      = "notes"
const val P_GOALS      = "goals"
const val P_CHECKED    = "checked"
const val P_SCHEDULE   = "schedule_entries"
const val P_PHOTO      = "photo_path"

// ── Serialization separators ──────────────────────────────────────────────────
const val GOAL_SEP  = "\n~~\n"
const val SCHED_SEP = "|||"

// ── Domain constants ──────────────────────────────────────────────────────────
val ALL_DAYS     = listOf("LUN", "MAR", "MIE", "JUE", "VIE", "SAB", "DOM")
val SCHED_COLORS = listOf("1565C0", "E53935", "2E7D32", "6A1B9A", "E65100", "00838F", "4527A0")

// ── Domain models ─────────────────────────────────────────────────────────────
/** Groups related profile primitives — eliminates Primitive Obsession. */
data class UserProfile(
    val name: String      = "Usuario FitPoli",
    val email: String     = "usuario@fitpoli.com",
    val weight: String    = "70",
    val height: String    = "170",
    val fat: String       = "15",
    val photoPath: String = "",
)

data class SchedEntry(val days: List<String>, val activity: String, val colorHex: String)

val DEFAULT_SCHEDULE = listOf(
    SchedEntry(listOf("LUN", "MIE", "VIE"), "Fuerza",      "1565C0"),
    SchedEntry(listOf("MAR", "JUE"),         "Cardio HIIT", "E53935"),
)

// ── Business logic ────────────────────────────────────────────────────────────
/** Extension keeps IMC logic close to the model it operates on. */
fun UserProfile.calcImc(): String {
    val wf = weight.toFloatOrNull() ?: return "-"
    val hm = (height.toFloatOrNull() ?: return "-") / 100f
    return if (hm > 0f) "%.1f".format(wf / (hm * hm)) else "-"
}

// ── Codec helpers ─────────────────────────────────────────────────────────────
fun encodeGoals(l: List<String>)    = l.joinToString(GOAL_SEP)
fun decodeGoals(s: String)          = if (s.isBlank()) emptyList() else s.split(GOAL_SEP)
fun encodeChecked(l: List<Boolean>) = l.joinToString(",") { if (it) "1" else "0" }
fun decodeChecked(s: String)        = if (s.isBlank()) emptyList() else s.split(",").map { it == "1" }

fun encodeSchedule(l: List<SchedEntry>) =
    l.joinToString(GOAL_SEP) { "${it.days.joinToString(",")}$SCHED_SEP${it.activity}$SCHED_SEP${it.colorHex}" }

fun decodeSchedule(s: String): List<SchedEntry> {
    if (s.isBlank()) return emptyList()
    return s.split(GOAL_SEP).mapNotNull { e ->
        val p = e.split(SCHED_SEP)
        if (p.size >= 3) SchedEntry(
            p[0].split(",").map { it.trim() }.filter { it.isNotBlank() }, p[1], p[2]
        ) else null
    }
}

fun schedColor(hex: String): Color = try {
    Color(android.graphics.Color.parseColor("#$hex"))
} catch (_: Exception) { Color(0xFF1565C0) }

// ── Semantic UI colours — no magic hex scattered across composables ────────────
object PerfilColors {
    val textPrimary   = Color(0xFF212121)
    val textSecondary = Color(0xFF9E9E9E)
    val textMuted     = Color(0xFF616161)
    val textHint      = Color(0xFF757575)
    val textDark      = Color(0xFF424242)
    val deleteRed     = Color(0xFFE53935)
    val goalGold      = Color(0xFFFFB300)
    val surface       = Color(0xFFF5F5F5)
    val border        = Color(0xFFBDBDBD)
    val accent        = Color(0xFF1565C0)
}
