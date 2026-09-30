package com.capa8.fitnesspersonalapp.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.capa8.fitnesspersonalapp.ui.theme.FitnessBlue
import com.capa8.fitnesspersonalapp.ui.theme.FitnessBlueDark
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * SRP: renders profile header only — photo picker, display/edit toggle.
 * Sub-composables live in PerfilHeaderParts.kt to stay ≤ 100 lines.
 * Clean Code: UserProfile data class reduces param count from 10 → 6.
 */
@Composable
internal fun PerfilHeaderCard(
    profile: UserProfile,
    tmpProfile: UserProfile,
    editMode: Boolean,
    wfc: TextFieldColors,
    onTmpChange: (UserProfile) -> Unit,
    onPhotoPath: (String) -> Unit,
    onEdit: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val dest = File(context.filesDir, "profile_photo.jpg")
        // Copia en hilo IO para no bloquear el main thread.
        // onPhotoPath solo se llama si la copia fue exitosa → evita guardar
        // rutas inválidas en SharedPreferences cuando openInputStream falla.
        scope.launch(Dispatchers.IO) {
            val ok = try {
                context.contentResolver.openInputStream(uri)?.use { ins ->
                    dest.outputStream().use { out -> ins.copyTo(out) }
                    true
                } ?: false
            } catch (_: Exception) { false }
            if (ok) withContext(Dispatchers.Main) { onPhotoPath(dest.absolutePath) }
        }
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(FitnessBlueDark, FitnessBlue)))) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                HeaderActions(editMode, onCancel, onSave, onEdit)
                ProfileAvatar(profile.photoPath, editMode) { if (editMode) photoPicker.launch("image/*") }
                Spacer(Modifier.height(10.dp))
                if (editMode) EditableIdentity(tmpProfile, wfc, onTmpChange)
                else DisplayIdentity(profile)
                Spacer(Modifier.height(16.dp))
                if (editMode) EditableStats(tmpProfile, wfc, onTmpChange)
                else DisplayStats(profile)
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}
