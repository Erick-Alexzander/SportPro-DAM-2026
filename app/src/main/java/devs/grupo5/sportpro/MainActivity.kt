package devs.grupo5.sportpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.ui.theme.SportProTheme

// Colores según el Figma
val DarkBackground = Color(0xFF121414)
val CardBackground = Color(0xFF1C2220)
val PrimaryGreen = Color(0xFF00A86B)
val BadgeGreen = Color(0xFF0D533A)
val TextGray = Color(0xFFA0A5A3)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SportProTheme {
                ProfileScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil", color = Color.White, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { /* Acción de volver */ }) {
                        Text("<", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Tarjeta de Perfil
            ProfileHeaderCard()

            // 2. Información de Cuenta
            AccountInfoCard()
        }
    }
}

@Composable
fun ProfileHeaderCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF8C3A1E)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CM",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Carlos Mendoza", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = BadgeGreen,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Jugador",
                    color = PrimaryGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Academia SportPro · Primera", color = TextGray, fontSize = 13.sp)
        }
    }
}

@Composable
fun AccountInfoCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Información de cuenta", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)

            HorizontalDivider(color = Color(0xFF2C3431))

            InfoRow(label = "Nombre", value = "Carlos Mendoza")
            InfoRow(label = "Correo", value = "carlos.m@sportpro.cl")
            InfoRow(label = "Rol", value = "Jugador")
            InfoRow(label = "Academia", value = "Academia SportPro · Primera")
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextGray, fontSize = 13.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    SportProTheme {
        ProfileScreen()
    }
}