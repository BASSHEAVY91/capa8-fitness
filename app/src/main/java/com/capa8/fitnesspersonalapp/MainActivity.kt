package com.capa8.fitnesspersonalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import com.capa8.fitnesspersonalapp.ui.screens.MainScreen
import com.capa8.fitnesspersonalapp.ui.screens.SplashAnimScreen
import com.capa8.fitnesspersonalapp.ui.theme.FitnessPersonalAppTheme
import com.capa8.fitnesspersonalapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Single-activity entry point para FitPoli.
 *
 * Flujo de inicio:
 *  1. El SO dibuja windowBackground (splash_screen.xml estático) durante el cold-start.
 *  2. Compose muestra [SplashAnimScreen] mientras:
 *       a) Se reproduce la animación completa (mínimo ~2.4 s).
 *       b) [appReady] pasa a `true` (carga del perfil desde DataStore).
 *  3. Cuando ambas condiciones se cumplen, [MainScreen] aparece con fade-in.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_FitnessPersonalApp)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitnessPersonalAppTheme {
                var splashDone by remember { mutableStateOf(false) }
                var appReady   by remember { mutableStateOf(false) }

                // Pre-carga SharedPreferences en hilo IO para evitar jank en el main thread
                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) {
                        applicationContext.getSharedPreferences("fitpoli_perfil", Context.MODE_PRIVATE)
                    }
                    appReady = true
                }

                AnimatedContent(
                    targetState = splashDone,
                    transitionSpec = {
                        fadeIn(tween(400)) togetherWith fadeOut(tween(300))
                    },
                    label = "splash_to_main"
                ) { done ->
                    if (done) {
                        MainScreen()
                    } else {
                        SplashAnimScreen(
                            isReady    = appReady,
                            onFinished = { splashDone = true }
                        )
                    }
                }
            }
        }
    }
}
