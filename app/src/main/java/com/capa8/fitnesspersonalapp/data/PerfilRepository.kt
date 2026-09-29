package com.capa8.fitnesspersonalapp.data

import android.content.SharedPreferences
import com.capa8.fitnesspersonalapp.ui.screens.*

/**
 * SRP: single source of truth for profile persistence.
 * DIP: composables / ViewModel depend on this abstraction, never on SharedPreferences directly.
 * All SharedPreferences read/write lives here — UI layer is completely decoupled from storage.
 */
internal class PerfilRepository(private val prefs: SharedPreferences) {

    // ── Read ──────────────────────────────────────────────────────────────────

    fun loadProfile(): UserProfile = UserProfile(
        name      = prefs.getString(P_NAME,   "Usuario FitPoli")!!,
        email     = prefs.getString(P_EMAIL,  "usuario@fitpoli.com")!!,
        weight    = prefs.getString(P_WEIGHT, "70")!!,
        height    = prefs.getString(P_HEIGHT, "170")!!,
        fat       = prefs.getString(P_FAT,    "15")!!,
        photoPath = prefs.getString(P_PHOTO,  "")!!,
    )

    fun loadGoals(): List<String> =
        decodeGoals(prefs.getString(P_GOALS, DEFAULT_GOALS)!!)

    fun loadChecked(size: Int): List<Boolean> {
        val stored = decodeChecked(prefs.getString(P_CHECKED, "")!!)
        return List(size) { stored.getOrElse(it) { false } }
    }

    fun loadSchedule(): List<SchedEntry> =
        decodeSchedule(prefs.getString(P_SCHEDULE, "")!!).ifEmpty { DEFAULT_SCHEDULE }

    fun loadNotes(): String = prefs.getString(P_NOTES, "")!!

    // ── Write ─────────────────────────────────────────────────────────────────

    fun saveProfile(p: UserProfile) = prefs.edit()
        .putString(P_NAME,   p.name)
        .putString(P_EMAIL,  p.email)
        .putString(P_WEIGHT, p.weight)
        .putString(P_HEIGHT, p.height)
        .putString(P_FAT,    p.fat)
        .putString(P_PHOTO,  p.photoPath)
        .apply()

    fun saveGoals(goals: List<String>, checked: List<Boolean>) = prefs.edit()
        .putString(P_GOALS,   encodeGoals(goals))
        .putString(P_CHECKED, encodeChecked(checked))
        .apply()

    fun saveSchedule(sched: List<SchedEntry>) = prefs.edit()
        .putString(P_SCHEDULE, encodeSchedule(sched))
        .apply()

    fun saveNotes(notes: String) = prefs.edit()
        .putString(P_NOTES, notes)
        .apply()

    // ── Defaults ──────────────────────────────────────────────────────────────

    private companion object {
        const val DEFAULT_GOALS = "Correr 5 km\n~~\nPerder 5 kg\n~~\nGanar masa muscular"
    }
}
