package com.capa8.fitnesspersonalapp.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capa8.fitnesspersonalapp.ui.theme.ContentBackground
import com.capa8.fitnesspersonalapp.ui.theme.FitnessBlue
import com.capa8.fitnesspersonalapp.ui.theme.FitnessPersonalAppTheme

/**
 * Top application bar with branded blue background.
 *
 * When [sectionIcon] is provided the icon animates into view with a spring
 * bounce whenever the section changes, while the title slides in from the
 * right.  Both transitions exit to the left so navigation feels directional.
 * A subtle infinite pulse keeps the icon lively between transitions.
 *
 * @param title       Text displayed as the bar title.
 * @param sectionIcon Optional [ImageVector] that represents the current section.
 * @param onMenuClick Callback invoked when the hamburger (☰) icon is tapped.
 *                    Pass `null` to hide the icon (default).
 * @param modifier    Optional [Modifier].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessTopBar(
    title: String,
    sectionIcon: ImageVector? = null,
    onMenuClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // ── Continuous pulse on the section icon ──────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "iconPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    TopAppBar(
        navigationIcon = {
            if (onMenuClick != null) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menú",
                        tint = ContentBackground
                    )
                }
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // ── Animated title text (left) ────────────────────────────
                AnimatedContent(
                    targetState = title,
                    transitionSpec = {
                        // New title slides in from right, old slides out to left
                        (slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth / 3 },
                            animationSpec  = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness    = Spring.StiffnessMedium
                            )
                        ) + fadeIn(animationSpec = tween(250))) togetherWith
                        (slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth / 3 },
                            animationSpec = tween(200)
                        ) + fadeOut(animationSpec = tween(150)))
                    },
                    label = "titleTextAnim"
                ) { titleText ->
                    Text(
                        text       = titleText,
                        color      = ContentBackground,
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign  = TextAlign.Start
                    )
                }

                // ── Push icon to the right ────────────────────────────────
                Spacer(modifier = Modifier.weight(1f))

                // ── Animated section icon (right) ─────────────────────────
                AnimatedContent(
                    targetState = sectionIcon,
                    transitionSpec = {
                        // Enter: spring-bounce scale + fade in
                        (scaleIn(
                            initialScale = 0.2f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness    = Spring.StiffnessMediumLow
                            )
                        ) + fadeIn(animationSpec = tween(200))) togetherWith
                        // Exit: shrink + fade out quickly
                        (scaleOut(targetScale = 0.2f, animationSpec = tween(150)) +
                                fadeOut(animationSpec = tween(150)))
                    },
                    label = "sectionIconAnim"
                ) { icon ->
                    if (icon != null) {
                        Icon(
                            imageVector        = icon,
                            contentDescription = null,
                            tint               = ContentBackground,
                            modifier           = Modifier
                                .size(24.dp)
                                .scale(pulseScale)
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor    = FitnessBlue,
            titleContentColor = ContentBackground
        ),
        modifier = modifier
    )
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun FitnessTopBarWithIconPreview() {
    FitnessPersonalAppTheme {
        FitnessTopBar(
            title       = "Perfil",
            sectionIcon = Icons.Filled.Person,
            onMenuClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FitnessTopBarNoIconPreview() {
    FitnessPersonalAppTheme {
        FitnessTopBar(
            title       = "FitPoli",
            onMenuClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FitnessTopBarFitnessCenterPreview() {
    FitnessPersonalAppTheme {
        FitnessTopBar(
            title       = "Utilidades",
            sectionIcon = Icons.Filled.FitnessCenter,
            onMenuClick = {}
        )
    }
}
