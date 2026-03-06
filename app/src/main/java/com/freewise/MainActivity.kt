package com.freewise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.freewise.ui.FreeWiseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bladeRunnerScheme = darkColorScheme(
            primary = Color(0xFF22F0FF),
            secondary = Color(0xFFFF3CB7),
            tertiary = Color(0xFF8F5BFF),
            background = Color(0xFF08040F),
            surface = Color(0xFF130A22),
            onPrimary = Color.Black,
            onBackground = Color(0xFFF5ECFF),
            onSurface = Color(0xFFF5ECFF)
        )

        setContent {
            MaterialTheme(colorScheme = bladeRunnerScheme) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    FreeWiseApp()
                }
            }
        }
    }
}
