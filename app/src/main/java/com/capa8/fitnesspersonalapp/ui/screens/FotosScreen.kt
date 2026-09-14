package com.capa8.fitnesspersonalapp.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capa8.fitnesspersonalapp.ui.theme.ContentTextColor
import com.capa8.fitnesspersonalapp.ui.theme.ContentTitleColor
import com.capa8.fitnesspersonalapp.ui.theme.FitnessPersonalAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Fotos screen – photographic record of the user's physical progress.
 */

// Paso 1: modelo de datos. Ahora puede tener una foto real (imageUri) o usar el color de relleno
data class ProgressPhoto(
    val id: Int,
    val caption: String,
    val date: String,
    val colorHex: Long,
    val imageUri: Uri? = null
)

// Datos de ejemplo (mock) — se muestran hasta que el usuario agregue fotos reales
private fun sampleProgressPhotos(): List<ProgressPhoto> = listOf(
    ProgressPhoto(1, "Semana 1", "03/09/2026", 0xFFB8E0D2),
    ProgressPhoto(2, "Semana 4", "24/09/2026", 0xFFF6D3B0),
    ProgressPhoto(3, "Semana 8", "22/10/2026", 0xFFD8E2D4),
    ProgressPhoto(4, "Semana 12", "19/11/2026", 0xFFE4E2F5)
)

@Composable
fun FotosScreen(modifier: Modifier = Modifier) {
    var fotos by remember { mutableStateOf(sampleProgressPhotos()) }

    // Paso 3: selector de fotos nativo de Android (Photo Picker, sin permisos extra)
    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val nuevaFoto = ProgressPhoto(
                id = fotos.size + 1,
                caption = "Nueva foto",
                date = "Hoy",
                colorHex = 0xFFCCCCCC,
                imageUri = uri
            )
            fotos = fotos + nuevaFoto
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Fotos de Progreso",
            color = ContentTitleColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 28.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Registro fotográfico del progreso físico del usuario.",
            color = ContentTextColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(fotos) { foto ->
                PhotoCard(foto)
            }
            item {
                AddPhotoCard(
                    onClick = {
                        pickMedia.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
            }
        }
    }
}

// Paso 4: decodifica la imagen real usando solo herramientas de Android (sin librerías nuevas)
@Composable
private fun rememberBitmapFromUri(uri: Uri): ImageBitmap? {
    val context = LocalContext.current
    var bitmap by remember(uri) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(uri) {
        withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val decoded = BitmapFactory.decodeStream(stream)
                    bitmap = decoded?.asImageBitmap()
                }
            } catch (e: Exception) {
                // Si falla, se queda en null y se muestra el placeholder
            }
        }
    }
    return bitmap
}

@Composable
private fun PhotoCard(foto: ProgressPhoto) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.aspectRatio(0.85f)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(foto.colorHex)),
                contentAlignment = Alignment.Center
            ) {
                if (foto.imageUri != null) {
                    val bitmap = rememberBitmapFromUri(foto.imageUri)
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap,
                            contentDescription = foto.caption,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(text = "⏳", fontSize = 24.sp)
                    }
                } else {
                    Text(text = "📷", fontSize = 28.sp)
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = foto.caption,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ContentTitleColor
                )
                Text(
                    text = foto.date,
                    fontSize = 11.sp,
                    color = ContentTextColor
                )
            }
        }
    }
}

@Composable
private fun AddPhotoCard(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .aspectRatio(0.85f)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir foto")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Añadir foto", fontSize = 12.sp, color = ContentTextColor)
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun FotosScreenPreview() {
    FitnessPersonalAppTheme {
        FotosScreen()
    }
}
