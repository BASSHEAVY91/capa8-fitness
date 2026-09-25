package com.capa8.fitnesspersonalapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capa8.fitnesspersonalapp.ui.theme.FitnessBlue
import com.capa8.fitnesspersonalapp.ui.theme.FitnessBlueDark
import com.capa8.fitnesspersonalapp.ui.theme.FitnessPersonalAppTheme

private fun calcImc(w: String, h: String): String {
    val wf = w.toFloatOrNull() ?: return "-"
    val hm = (h.toFloatOrNull() ?: return "-") / 100f
    return if (hm > 0f) "%.1f".format(wf / (hm * hm)) else "-"
}

private val GOALS = listOf(
    "Perder 5 kg en 3 meses",
    "Correr 5 km sin parar",
    "Aumentar fuerza en piernas"
)

private data class ScheduleEntry(val days: List<String>, val activity: String, val color: Color)

private val SCHEDULE = listOf(
    ScheduleEntry(listOf("LUN", "MIE", "VIE"), "Fuerza", Color(0xFF1565C0)),
    ScheduleEntry(listOf("MAR", "JUE"), "Cardio HIIT", Color(0xFFE53935))
)

@Composable
fun PerfilScreen(modifier: Modifier = Modifier) {
    val name = rememberSaveable { "Juan Perez" }
    val email = rememberSaveable { "juan@fitpoli.com" }
    val weight = rememberSaveable { "72" }
    val height = rememberSaveable { "175" }
    val fat = rememberSaveable { "12%" }
    val notes = rememberSaveable { "Mantener hidratacion. Dormir 8 horas." }
    val checked = remember { mutableStateListOf(*Array(GOALS.size) { false }) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(FitnessBlueDark, FitnessBlue)))
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(3.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        email,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.height(18.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val imc = calcImc(weight, height)
                        StatChip("$weight kg", "Peso", Modifier.weight(1f))
                        StatChip("$height cm", "Altura", Modifier.weight(1f))
                        StatChip(imc, "IMC", Modifier.weight(1f))
                        StatChip(fat, "Grasa", Modifier.weight(1f))
                    }
                }
            }
        }

        ProfileCard(Icons.Filled.EmojiEvents, "Metas de Entrenamiento", Color(0xFFFFA726)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                GOALS.forEachIndexed { i, g ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { checked[i] = !checked[i] }
                            .background(
                                if (checked[i]) Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (checked[i]) Icons.Filled.CheckCircle
                            else Icons.Filled.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (checked[i]) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            g,
                            fontSize = 14.sp,
                            color = if (checked[i]) Color(0xFF2E7D32) else Color(0xFF424242)
                        )
                    }
                }
            }
        }

        ProfileCard(Icons.Filled.Schedule, "Rutina Actual", Color(0xFF29B6F6)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SCHEDULE.forEach { entry ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(entry.color.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            entry.days.forEach { d ->
                                Box(
                                    Modifier
                                        .background(entry.color, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        d,
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            entry.activity,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = entry.color
                        )
                    }
                }
            }
        }

        ProfileCard(Icons.Filled.EditNote, "Notas Personales", Color(0xFF66BB6A)) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F8E9), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(notes, fontSize = 14.sp, color = Color(0xFF33691E), lineHeight = 20.sp)
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun StatChip(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            value,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Text(
            label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProfileCard(
    icon: ImageVector,
    title: String,
    iconTint: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
                Text(title, fontWeight = FontWeight.Bold, color = FitnessBlue, fontSize = 16.sp)
            }
            content()
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun PerfilScreenPreview() {
    FitnessPersonalAppTheme { PerfilScreen() }
}
