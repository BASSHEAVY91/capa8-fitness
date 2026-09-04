package com.capa8.fitnesspersonalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.capa8.fitnesspersonalapp.ui.screens.MainScreen
import com.capa8.fitnesspersonalapp.ui.theme.FitnessPersonalAppTheme

/**
 * Single-activity entry point for the Fitness Personal App.
 *
 * All UI is rendered via Jetpack Compose. [MainScreen] acts as the root
 * composable and manages the persistent side-navigation layout.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitnessPersonalAppTheme {
                MainScreen()
            }
        }
    }
}
