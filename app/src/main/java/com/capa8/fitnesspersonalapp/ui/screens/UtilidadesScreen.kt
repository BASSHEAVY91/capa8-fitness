package com.capa8.fitnesspersonalapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capa8.fitnesspersonalapp.ui.theme.ContentTextColor
import com.capa8.fitnesspersonalapp.ui.theme.FitnessBlue
import com.capa8.fitnesspersonalapp.ui.theme.FitnessBlueDark
import com.capa8.fitnesspersonalapp.ui.theme.FitnessPersonalAppTheme
import kotlinx.coroutines.delay

// ── Helpers ───────────────────────────────────────────────────────────────────

private data class ImcCategory(val label: String, val color: Color)

private fun calcImc(w: Float, h: Float): Float {
    if (h <= 0f || w <= 0f) return 0f
    val hm = h / 100f
    return w / (hm * hm)
}

private fun imcCategory(v: Float): ImcCategory = when {
    v <= 0f   -> ImcCategory("–",         Color.Gray)
    v < 18.5f -> ImcCategory("Bajo peso", Color(0xFFFFA726))
    v < 25f   -> ImcCategory("Normal",    Color(0xFF4CAF50))
    v < 30f   -> ImcCategory("Sobrepeso", Color(0xFFFF7043))
    else      -> ImcCategory("Obesidad",  Color(0xFFF44336))
}

private fun formatTime(s: Int) =
    "%02d:%02d:%02d".format(s / 3600, (s % 3600) / 60, s % 60)

private data class WeekRecord(val label: String, val kg: Float)
private val WEEKLY = listOf(
    WeekRecord("Semana 1", 68f),
    WeekRecord("Semana 2", 70f),
    WeekRecord("Semana 3", 71.5f),
    WeekRecord("Semana 4", 72f)
)

// ── Reusable header row ────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = FitnessBlue,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = FitnessBlue,
            fontSize = 17.sp
        )
    }
}

// ── Main screen ────────────────────────────────────────────────────────────────

@Composable
fun UtilidadesScreen(modifier: Modifier = Modifier) {

    // ── Cronómetro state ──────────────────────────────────────────────────────
    var totalSeconds by rememberSaveable { mutableIntStateOf(0) }
    var isRunning    by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1_000L)
            totalSeconds++
        }
    }

    // ── IMC state ─────────────────────────────────────────────────────────────
    var pesoText   by rememberSaveable { mutableStateOf("72") }
    var alturaText by rememberSaveable { mutableStateOf("175") }
    val imc = remember(pesoText, alturaText) {
        calcImc(pesoText.toFloatOrNull() ?: 0f, alturaText.toFloatOrNull() ?: 0f)
    }
    val cat = imcCategory(imc)

    // ── Root layout ───────────────────────────────────────────────────────────
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // ── 1. CRONÓMETRO ────────────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    SectionHeader(Icons.Filled.Timer, "Cronómetro")
                }

                Spacer(Modifier.height(16.dp))

                // Auto-sizing timer: fills available width regardless of screen size
                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    // 8 chars "00:00:00", fit them into available width with some padding
                    val timerFontSize = (maxWidth * 0.115f).value.sp
                    Text(
                        text = formatTime(totalSeconds),
                        fontSize = timerFontSize,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = FitnessBlueDark,
                        letterSpacing = 1.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Icon-only circular buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFF4CAF50), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { if (!isRunning) isRunning = true }) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = "Iniciar",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    // Stop
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFFF44336), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { isRunning = false }) {
                            Icon(
                                imageVector = Icons.Filled.Stop,
                                contentDescription = "Parar",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    // Reset
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(FitnessBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { isRunning = false; totalSeconds = 0 }) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Reiniciar",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }

        // ── 2. CALCULADORA IMC ───────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                SectionHeader(Icons.Filled.Calculate, "Calculadora IMC")

                Spacer(Modifier.height(16.dp))

                // Inputs: Peso | Altura in a comfortable two-column row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Peso (kg)",
                            fontSize = 13.sp,
                            color = ContentTextColor,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = pesoText,
                            onValueChange = { pesoText = it },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FitnessBlue,
                                unfocusedBorderColor = Color(0xFFBDBDBD)
                            )
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Altura (cm)",
                            fontSize = 13.sp,
                            color = ContentTextColor,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = alturaText,
                            onValueChange = { alturaText = it },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FitnessBlue,
                                unfocusedBorderColor = Color(0xFFBDBDBD)
                            )
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Result badge – full width, colour-coded
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cat.color.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                        .padding(vertical = 22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (imc > 0f) {
                            Text(
                                "%.1f".format(imc),
                                fontSize = 40.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = cat.color
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                cat.label,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = cat.color
                            )
                        } else {
                            Text("Ingrese datos", color = Color.Gray, fontSize = 15.sp)
                        }
                    }
                }
            }
        }

        // ── 3. REGISTRO DE PROGRESO ──────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                SectionHeader(Icons.Filled.TrendingUp, "Registro de Progreso")

                Spacer(Modifier.height(20.dp))

                WEEKLY.forEachIndexed { i, record ->
                    WeekProgressRow(
                        label    = record.label,
                        weightKg = record.kg,
                        maxKg    = 80f
                    )
                    if (i < WEEKLY.lastIndex) Spacer(Modifier.height(18.dp))
                }
            }
        }

        // Bottom spacing so last card isn't flush with edge
        Spacer(Modifier.height(8.dp))
    }
}

// ── Progress row composable ────────────────────────────────────────────────────

@Composable
private fun WeekProgressRow(
    label: String,
    weightKg: Float,
    maxKg: Float,
    modifier: Modifier = Modifier
) {
    val progress = if (maxKg > 0f) (weightKg / maxKg).coerceIn(0f, 1f) else 0f
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 15.sp, color = ContentTextColor, fontWeight = FontWeight.Medium)
            Text(
                "${"%.1f".format(weightKg)} kg",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = FitnessBlueDark
            )
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = FitnessBlue,
            trackColor = Color(0xFFE3F2FD),
            strokeCap = StrokeCap.Round
        )
    }
}

// ── Preview ────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun UtilidadesScreenPreview() {
    FitnessPersonalAppTheme {
        UtilidadesScreen()
    }
}
