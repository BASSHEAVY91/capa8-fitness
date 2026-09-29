package com.capa8.fitnesspersonalapp.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capa8.fitnesspersonalapp.ui.theme.FitnessBlueDark

/** Sub-composables for PerfilHeaderCard — SRP: each function has one reason to change. */

@Composable
internal fun HeaderActions(editMode: Boolean, onCancel: () -> Unit, onSave: () -> Unit, onEdit: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        if (editMode) {
            TextButton(onClick = onCancel) { Text("Cancelar", color = Color.White.copy(.8f), fontSize = 13.sp) }
            Spacer(Modifier.width(6.dp))
            Button(onClick = onSave, colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = FitnessBlueDark),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)) {
                Text("Guardar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        } else {
            IconButton(onClick = onEdit) { Icon(Icons.Filled.Edit, "Editar", tint = Color.White) }
        }
    }
}

@Composable
internal fun ProfileAvatar(photoPath: String, editMode: Boolean, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.BottomEnd) {
        Box(Modifier.size(80.dp).clip(CircleShape).background(Color.White.copy(.25f)).clickable(onClick = onClick),
            contentAlignment = Alignment.Center) {
            val bmp = remember(photoPath) { if (photoPath.isNotBlank()) BitmapFactory.decodeFile(photoPath) else null }
            if (bmp != null) Image(bmp.asImageBitmap(), null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            else Icon(Icons.Filled.Person, null, tint = Color.White, modifier = Modifier.size(48.dp))
        }
        if (editMode) Box(Modifier.size(22.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.CameraAlt, null, tint = FitnessBlueDark, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
internal fun EditableIdentity(tmp: UserProfile, wfc: TextFieldColors, onChange: (UserProfile) -> Unit) {
    OutlinedTextField(tmp.name, { onChange(tmp.copy(name = it)) }, label = { Text("Nombre") },
        singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = wfc)
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(tmp.email, { onChange(tmp.copy(email = it)) }, label = { Text("Email") },
        singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = wfc,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
}

@Composable
internal fun DisplayIdentity(profile: UserProfile) {
    Text(profile.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
    Spacer(Modifier.height(4.dp))
    Text(profile.email, fontSize = 13.sp, color = Color.White.copy(.8f))
}

@Composable
internal fun EditableStats(tmp: UserProfile, wfc: TextFieldColors, onChange: (UserProfile) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(tmp.weight, { onChange(tmp.copy(weight = it)) }, label = { Text("Peso (kg)") },
            singleLine = true, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(8.dp), colors = wfc)
        OutlinedTextField(tmp.height, { onChange(tmp.copy(height = it)) }, label = { Text("Altura (cm)") },
            singleLine = true, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(8.dp), colors = wfc)
        OutlinedTextField(tmp.fat, { onChange(tmp.copy(fat = it)) }, label = { Text("Grasa %") },
            singleLine = true, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(8.dp), colors = wfc)
    }
}

@Composable
internal fun DisplayStats(profile: UserProfile) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        StatChip("${profile.weight} kg", "Peso")
        StatChip(profile.calcImc(), "IMC")
        StatChip("${profile.height} cm", "Altura")
        StatChip("${profile.fat}%", "Grasa")
    }
}
