package com.medialarma.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import com.medialarma.app.ui.navigation.AppNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Si tu plantilla trae un tema propio (p. ej. MediAlarmaTheme), úsalo aquí en lugar de MaterialTheme.
            MaterialTheme {
                AppNavHost()
            }
        }
    }
}
