package devs.grupo5.sportpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.tooling.preview.Preview
import devs.grupo5.sportpro.ui.screens.RegisterScreen
import devs.grupo5.sportpro.ui.theme.SportProTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SportProTheme(darkTheme = true, dynamicColor = false) {
                RegisterScreen(
                    onBack = { finish() },
                    onLogin = { },
                    onContinue = { }
                )
            }
        }
    }
}
