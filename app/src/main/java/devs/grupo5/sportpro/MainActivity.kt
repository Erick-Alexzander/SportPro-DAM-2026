package devs.grupo5.sportpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import devs.grupo5.sportpro.presentation.auth.AuthNavGraph
import devs.grupo5.sportpro.ui.theme.SportProTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SportProTheme(darkTheme = true, dynamicColor = false) {
                AuthNavGraph()
            }
        }
    }
}
