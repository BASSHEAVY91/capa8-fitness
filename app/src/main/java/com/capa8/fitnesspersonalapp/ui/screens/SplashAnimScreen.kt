package com.capa8.fitnesspersonalapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlin.math.cos
import kotlin.math.sin

// ─── Brand colours ─────────────────────────────────────────────────────────────
private val BrandBlue   = Color(0xFF4A90D9)
private val BrandDark   = Color(0xFF0D1B2A)
private val BrandAccent = Color(0xFFFF6B35)

// ─── Phrase data ───────────────────────────────────────────────────────────────
private enum class PhraseIcon { ARROW_UP, FLAME, DUMBBELL_MINI, STAR, BOLT }

private val phrases = listOf(
    "Tu mejor versión empieza hoy"              to PhraseIcon.ARROW_UP,
    "Un día más, un paso más fuerte"            to PhraseIcon.FLAME,
    "El único mal entreno es el que no hiciste" to PhraseIcon.DUMBBELL_MINI,
    "Suda ahora, brilla después"                to PhraseIcon.STAR,
    "Cada repetición cuenta. ¡Vamos!"           to PhraseIcon.BOLT
)

// ══════════════════════════════════════════════════════════════════════════════
// 1. LARGE DUMBBELL LOGO  (Canvas, no emoji)
// ══════════════════════════════════════════════════════════════════════════════
@Composable
fun DumbbellIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    accentColor: Color = BrandAccent
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Grip bar
        val barH    = h * 0.16f
        val barW    = w * 0.44f
        val barLeft = (w - barW) / 2f
        val barTop  = (h - barH) / 2f
        drawRoundRect(
            color        = color,
            topLeft      = Offset(barLeft, barTop),
            size         = Size(barW, barH),
            cornerRadius = CornerRadius(barH / 2f)
        )

        // Collar rings
        val collarW = w * 0.065f
        val collarH = h * 0.32f
        val collarY = (h - collarH) / 2f
        drawRoundRect(
            color        = accentColor,
            topLeft      = Offset(barLeft - collarW, collarY),
            size         = Size(collarW, collarH),
            cornerRadius = CornerRadius(3.dp.toPx())
        )
        drawRoundRect(
            color        = accentColor,
            topLeft      = Offset(barLeft + barW, collarY),
            size         = Size(collarW, collarH),
            cornerRadius = CornerRadius(3.dp.toPx())
        )

        // Weight plates left
        val opW = w * 0.10f; val opH = h * 0.74f
        val ipW = w * 0.07f; val ipH = h * 0.56f
        val lFar = barLeft - collarW - opW - ipW

        drawRoundRect(color = color.copy(alpha = 0.60f),
            topLeft = Offset(lFar, (h - ipH) / 2f), size = Size(ipW, ipH),
            cornerRadius = CornerRadius(3.dp.toPx()))
        drawRoundRect(color = color,
            topLeft = Offset(lFar + ipW, (h - opH) / 2f), size = Size(opW, opH),
            cornerRadius = CornerRadius(4.dp.toPx()))

        // Weight plates right
        val rNear = barLeft + barW + collarW
        drawRoundRect(color = color,
            topLeft = Offset(rNear, (h - opH) / 2f), size = Size(opW, opH),
            cornerRadius = CornerRadius(4.dp.toPx()))
        drawRoundRect(color = color.copy(alpha = 0.60f),
            topLeft = Offset(rNear + opW, (h - ipH) / 2f), size = Size(ipW, ipH),
            cornerRadius = CornerRadius(3.dp.toPx()))
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// 2. SMALL PHRASE ICONS  (DrawScope extensions)
// ══════════════════════════════════════════════════════════════════════════════

/** ↑ Arrow — "growth". */
private fun DrawScope.drawArrowUp(color: Color) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val s  = minOf(size.width, size.height)
    val sw = s * 0.14f
    val path = Path().apply {
        moveTo(cx, cy + s * 0.36f)
        lineTo(cx, cy - s * 0.12f)
        moveTo(cx - s * 0.28f, cy - s * 0.02f)
        lineTo(cx, cy - s * 0.40f)
        lineTo(cx + s * 0.28f, cy - s * 0.02f)
    }
    drawPath(path, color = color,
        style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

/** Flame shape — "fire / energy". */
private fun DrawScope.drawFlame(color: Color) {
    val w = size.width; val h = size.height
    val body = Path().apply {
        moveTo(w * .50f, h * .04f)
        cubicTo(w * .82f, h * .22f, w * .88f, h * .48f, w * .68f, h * .62f)
        cubicTo(w * .84f, h * .38f, w * .62f, h * .50f, w * .58f, h * .68f)
        cubicTo(w * .56f, h * .84f, w * .44f, h * .96f, w * .30f, h * .86f)
        cubicTo(w * .08f, h * .72f, w * .14f, h * .44f, w * .34f, h * .54f)
        cubicTo(w * .18f, h * .38f, w * .18f, h * .18f, w * .50f, h * .04f)
        close()
    }
    drawPath(body, color = color)
    val inner = Path().apply {
        moveTo(w * .50f, h * .28f)
        cubicTo(w * .64f, h * .42f, w * .66f, h * .60f, w * .54f, h * .70f)
        cubicTo(w * .49f, h * .80f, w * .38f, h * .80f, w * .34f, h * .70f)
        cubicTo(w * .26f, h * .56f, w * .36f, h * .42f, w * .50f, h * .28f)
        close()
    }
    drawPath(inner, color = Color.White.copy(alpha = 0.28f))
}

/** Mini dumbbell — for phrase icon. */
private fun DrawScope.drawDumbbellMini(color: Color) {
    val w = size.width; val h = size.height
    val bH = h * .18f; val bW = w * .44f
    val bX = (w - bW) / 2f; val bY = (h - bH) / 2f
    drawRoundRect(color = color, topLeft = Offset(bX, bY),
        size = Size(bW, bH), cornerRadius = CornerRadius(bH / 2f))
    val pH = h * .72f; val pW = w * .13f
    drawRoundRect(color = color, topLeft = Offset(bX - pW, (h - pH) / 2f),
        size = Size(pW, pH), cornerRadius = CornerRadius(2.dp.toPx()))
    drawRoundRect(color = color, topLeft = Offset(bX + bW, (h - pH) / 2f),
        size = Size(pW, pH), cornerRadius = CornerRadius(2.dp.toPx()))
}

/** 4-point sparkle / star. */
private fun DrawScope.drawStar(color: Color) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val r1 = minOf(size.width, size.height) * 0.44f   // long ray
    val r2 = minOf(size.width, size.height) * 0.18f   // short ray
    val path = Path()
    val totalPoints = 8
    for (i in 0 until totalPoints) {
        val angle = Math.PI / 4.0 * i - Math.PI / 2.0
        val r = if (i % 2 == 0) r1 else r2
        val x = cx + (r * cos(angle)).toFloat()
        val y = cy + (r * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color = color)
}

/** Lightning bolt — "energy / power". */
private fun DrawScope.drawBolt(color: Color) {
    val w = size.width; val h = size.height
    val path = Path().apply {
        moveTo(w * .62f, h * .02f)
        lineTo(w * .28f, h * .50f)
        lineTo(w * .52f, h * .50f)
        lineTo(w * .36f, h * .98f)
        lineTo(w * .72f, h * .44f)
        lineTo(w * .48f, h * .44f)
        close()
    }
    drawPath(path, color = color)
    // subtle inner shine
    val shine = Path().apply {
        moveTo(w * .56f, h * .08f)
        lineTo(w * .36f, h * .46f)
        lineTo(w * .46f, h * .46f)
    }
    drawPath(shine, color = Color.White.copy(alpha = 0.30f),
        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
}

// ══════════════════════════════════════════════════════════════════════════════
// 3. SMALL ICON COMPOSABLE  (wraps DrawScope helpers)
// ══════════════════════════════════════════════════════════════════════════════
@Composable
private fun PhraseIconView(icon: PhraseIcon, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        when (icon) {
            PhraseIcon.ARROW_UP      -> drawArrowUp(BrandAccent)
            PhraseIcon.FLAME         -> drawFlame(BrandAccent)
            PhraseIcon.DUMBBELL_MINI -> drawDumbbellMini(BrandAccent)
            PhraseIcon.STAR          -> drawStar(BrandAccent)
            PhraseIcon.BOLT          -> drawBolt(BrandAccent)
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// 4. MAIN SPLASH SCREEN
// ══════════════════════════════════════════════════════════════════════════════
/**
 * Full-screen animated splash / loading screen.
 *
 * Permanece visible hasta que se cumplan **ambas** condiciones:
 *   1. La animación mínima (~2.4 s) haya terminado.
 *   2. [isReady] sea `true` (datos de la app ya cargados).
 *
 * Timeline de animación:
 *   0 ms  → gradient background fade in
 *  150 ms  → dumbbell logo spring-bounce
 *  600 ms  → pulse ring starts looping
 *  650 ms  → "FitPoli" slides up
 *  850 ms  → motivational phrase + icon fade in
 * ~2 400 ms → espera [isReady] y luego llama [onFinished]
 *
 * @param isReady  `true` cuando la app ha terminado de inicializarse.
 *                 Si no se pasa (default `true`) el splash actúa como antes.
 * @param onFinished  Lambda llamada al terminar la transición.
 */
@Composable
fun SplashAnimScreen(isReady: Boolean = true, onFinished: () -> Unit) {

    // rememberUpdatedState garantiza que el snapshotFlow dentro del
    // LaunchedEffect siempre lee el valor MÁS RECIENTE de isReady,
    // evitando el closure-stale donde el coroutine capturaba `false` para siempre.
    val currentIsReady by rememberUpdatedState(isReady)

    val (phraseText, phraseIcon) = remember { phrases.random() }

    val bgAlpha     = remember { Animatable(1f) }   // empieza opaco: mismo color que el splash del sistema → sin flash
    val logoScale   = remember { Animatable(0f) }
    val logoAlpha   = remember { Animatable(0f) }
    val nameOffsetY = remember { Animatable(44f) }
    val nameAlpha   = remember { Animatable(0f) }
    val phraseAlpha = remember { Animatable(0f) }
    val pulseScale  = remember { Animatable(1f) }
    val pulseAlpha  = remember { Animatable(0.55f) }

    // Main animation sequence
    LaunchedEffect(Unit) {
        // bgAlpha ya es 1f desde el inicio – el fondo azul de Compose coincide con el
        // splash del sistema (#4A90D9), por lo que la transición es imperceptible.
        delay(120.milliseconds)
        logoAlpha.animateTo(1f, tween(220))
        logoScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
        delay(80.milliseconds)
        nameOffsetY.animateTo(0f, tween(380, easing = EaseOutCubic))
        nameAlpha.animateTo(1f, tween(380))
        delay(180.milliseconds)
        phraseAlpha.animateTo(1f, tween(480))
        // Espera mínima visual (da tiempo a leer la frase)
        delay(1200.milliseconds)
        // Bloquea hasta que la app esté lista (carga de datos)
        snapshotFlow { currentIsReady }.first { it }
        onFinished()
    }

    // Pulse ring — independent infinite loop
    LaunchedEffect(Unit) {
        delay(560.milliseconds)
        while (true) {
            pulseScale.animateTo(1.60f, tween(720, easing = EaseOutCubic))
            pulseAlpha.animateTo(0f, tween(480))
            pulseScale.snapTo(1f)
            pulseAlpha.snapTo(0.50f)
            delay(180.milliseconds)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(bgAlpha.value)
            .background(Brush.verticalGradient(listOf(BrandBlue, BrandDark))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {

            // ── Pulse ring + dumbbell logo ────────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(180.dp)
            ) {
                // Outer expanding ring
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .scale(pulseScale.value)
                        .alpha(pulseAlpha.value)
                        .background(BrandAccent.copy(alpha = 0.22f), CircleShape)
                )
                // Mid solid circle
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(BrandAccent.copy(alpha = 0.12f), CircleShape)
                )
                // Dumbbell logo (Canvas, no emoji)
                DumbbellIcon(
                    modifier = Modifier
                        .size(100.dp, 52.dp)
                        .scale(logoScale.value)
                        .alpha(logoAlpha.value)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // ── App name ──────────────────────────────────────────────────────
            Text(
                text = "FitPoli",
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp,
                modifier = Modifier
                    .offset(y = nameOffsetY.value.dp)
                    .alpha(nameAlpha.value)
            )
            Text(
                text = "Tu entrenador personal",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.62f),
                letterSpacing = 1.sp,
                modifier = Modifier
                    .offset(y = nameOffsetY.value.dp)
                    .alpha(nameAlpha.value)
            )

            Spacer(modifier = Modifier.height(38.dp))

            // ── Motivational phrase + custom icon ─────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(horizontal = 36.dp)
                    .alpha(phraseAlpha.value)
            ) {
                PhraseIconView(
                    icon = phraseIcon,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = phraseText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandAccent,
                    textAlign = TextAlign.Start,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
