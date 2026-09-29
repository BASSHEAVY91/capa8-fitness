package com.capa8.fitnesspersonalapp.ui.screens

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import com.capa8.fitnesspersonalapp.data.PerfilRepository

/**
 * SRP: owns ALL mutable state and business operations for PerfilScreen.
 * Composables become pure rendering functions — they call ViewModel methods and observe state.
 * OCP: adding a new profile field only requires changing UserProfile + Repository, not every composable.
 */
class PerfilViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = PerfilRepository(app.getSharedPreferences(PERFIL_PREFS, 0))

    // ── Persisted state ───────────────────────────────────────────────────────
    var profile  by mutableStateOf(repo.loadProfile());  private set
    var goals    by mutableStateOf(repo.loadGoals());    private set
    var checked  by mutableStateOf(repo.loadChecked(goals.size)); private set
    var schedule by mutableStateOf(repo.loadSchedule()); private set
    var notes    by mutableStateOf(repo.loadNotes());    private set

    // ── Edit-mode temporaries (never persisted until saveProfile) ─────────────
    var editMode   by mutableStateOf(false);   private set
    var tmpProfile by mutableStateOf(profile); private set

    // ── Commands ──────────────────────────────────────────────────────────────

    fun startEdit()              { tmpProfile = profile; editMode = true }
    fun cancelEdit()             { editMode = false }
    fun updateTmp(p: UserProfile){ tmpProfile = p }

    fun saveProfile() {
        profile = tmpProfile
        repo.saveProfile(profile)
        editMode = false
    }

    /** Called when the photo picker returns a saved path. */
    fun setPhotoPath(path: String) {
        profile = profile.copy(photoPath = path)
        repo.saveProfile(profile)
    }

    fun toggleGoal(idx: Int, value: Boolean) {
        checked = checked.toMutableList().also { it[idx] = value }
        repo.saveGoals(goals, checked)
    }

    fun removeGoal(idx: Int) {
        goals   = goals.toMutableList().also { it.removeAt(idx) }
        checked = checked.toMutableList().also { if (idx < it.size) it.removeAt(idx) }
        repo.saveGoals(goals, checked)
    }

    fun addGoal(text: String) {
        goals   = goals + text
        checked = checked + false
        repo.saveGoals(goals, checked)
    }

    fun removeScheduleEntry(idx: Int) {
        schedule = schedule.toMutableList().also { it.removeAt(idx) }
        repo.saveSchedule(schedule)
    }

    fun addScheduleEntry(entry: SchedEntry) {
        schedule = schedule + entry
        repo.saveSchedule(schedule)
    }

    fun updateNotes(text: String) {
        notes = text
        repo.saveNotes(notes)
    }
}
