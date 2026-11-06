package devs.grupo5.sportpro.presentation.player.community

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import devs.grupo5.sportpro.data.model.PlayerProfile
import devs.grupo5.sportpro.data.model.UserProfile
import devs.grupo5.sportpro.presentation.player.theme.PlayerBackground
import devs.grupo5.sportpro.presentation.player.theme.PlayerBorder
import devs.grupo5.sportpro.presentation.player.theme.PlayerBrown
import devs.grupo5.sportpro.presentation.player.theme.PlayerGreenContainer
import devs.grupo5.sportpro.presentation.player.theme.PlayerNeonGreen
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurface
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurfaceVariant
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextMuted
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextPrimary
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextSecondary
import devs.grupo5.sportpro.presentation.player.theme.SportProPlayerTheme
import java.time.LocalDate

@Composable
fun PublicProfileScreen(
    uid: String,
    onBackClick: () -> Unit,
    viewModel: PublicProfileViewModel = viewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(uid) {
        viewModel.load(uid)
    }

    SportProPlayerTheme {
        Scaffold(
            containerColor = PlayerBackground,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = PlayerTextPrimary
                        )
                    }
                    Text(
                        text = "Perfil",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayerTextPrimary
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = PlayerNeonGreen
                        )
                    }

                    profile == null -> {
                        Text(
                            text = errorMessage ?: "Perfil no disponible",
                            color = PlayerTextMuted,
                            fontSize = 15.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    else -> {
                        PublicProfileContent(profile = profile!!)
                    }
                }
            }
        }
    }
}

@Composable
private fun PublicProfileContent(profile: UserProfile) {
    val playerProfile = profile.playerProfile
    val privacy = playerProfile.privacySettings
    val isMinor = calculateAge(profile.birthDate) in 1..17
    val initials = listOf(profile.firstName, profile.lastName)
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "?" }

    val fullName = listOf(profile.firstName.trim(), profile.lastName.trim())
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { profile.email.substringBefore("@") }

    val displayName = when (privacy.fullNameVisibility) {
        "Público" -> fullName
        else -> profile.firstName.ifBlank { "Deportista" }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(PlayerBrown)
                    .border(2.dp, PlayerBrown, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (privacy.profilePhoto == "Público") {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = PlayerTextMuted,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = displayName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PlayerTextPrimary
            )

            if (playerProfile.category.isNotBlank() || playerProfile.position.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = listOf(playerProfile.positionLabel, playerProfile.category)
                        .filter { it.isNotBlank() && it != "—" }
                        .joinToString(" · "),
                    fontSize = 13.sp,
                    color = PlayerTextSecondary
                )
            }

            if (isMinor) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PlayerSurfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = PlayerNeonGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Cuenta Protegida",
                            color = PlayerNeonGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        ProfileCard(title = "Información deportiva") {
            InfoRow("Equipo", playerProfile.teamName.ifBlank { "Academia SportPro" })
            InfoDivider()
            InfoRow("Posición", playerProfile.positionLabel)
            InfoDivider()
            InfoRow("Dorsal", playerProfile.jerseyLabel)
            InfoDivider()
            InfoRow("Categoría", playerProfile.category.ifBlank { "—" })
            InfoDivider()
            InfoRow("Pie dominante", playerProfile.dominantFoot.ifBlank { "—" })
            if (playerProfile.heightCm > 0 || playerProfile.weightKg > 0.0) {
                InfoDivider()
                InfoRow(
                    "Físico",
                    listOf(playerProfile.heightLabel, playerProfile.weightLabel)
                        .joinToString(" · ")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (privacy.statsVisibility == "Público") {
            ProfileCard(title = "Estadísticas de temporada") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("Partidos", playerProfile.stats.matchesPlayed.toString())
                    StatItem("Goles", playerProfile.stats.goals.toString())
                    StatItem("Asist.", playerProfile.stats.assists.toString())
                }
            }

            if (playerProfile.attributes.hasData) {
                Spacer(modifier = Modifier.height(16.dp))
                ProfileCard(title = "Atributos") {
                    AttributeRow("Velocidad", playerProfile.attributes.speed)
                    AttributeRow("Tiro", playerProfile.attributes.shooting)
                    AttributeRow("Pase", playerProfile.attributes.passing)
                    AttributeRow("Defensa", playerProfile.attributes.defense)
                    AttributeRow("Físico", playerProfile.attributes.physical)
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, PlayerBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = PlayerSurfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = PlayerTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "El jugador mantiene sus estadísticas privadas.",
                        color = PlayerTextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = PlayerTextPrimary,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = PlayerTextSecondary, fontSize = 13.sp)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            color = PlayerTextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun InfoDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 10.dp),
        thickness = 0.5.dp,
        color = PlayerBorder
    )
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = PlayerNeonGreen,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(text = label, color = PlayerTextSecondary, fontSize = 12.sp)
    }
}

@Composable
private fun AttributeRow(label: String, value: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.sp, color = PlayerTextSecondary)
            Text(
                text = "$value",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PlayerTextPrimary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { value.coerceIn(0, 100) / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = PlayerNeonGreen,
            trackColor = PlayerSurfaceVariant
        )
    }
}

private fun calculateAge(birthDate: String?): Int {
    if (birthDate.isNullOrBlank()) return 0
    return try {
        val parts = birthDate.split("/", "-", ".").mapNotNull { it.trim().toIntOrNull() }
        if (parts.size != 3) return 0
        val (day, month, year) = if (parts[0] > 1000) {
            Triple(parts[2], parts[1], parts[0])
        } else {
            Triple(parts[0], parts[1], parts[2])
        }
        val today = LocalDate.now()
        val birth = LocalDate.of(year, month, day)
        var age = today.year - birth.year
        if (today.monthValue < birth.monthValue ||
            (today.monthValue == birth.monthValue && today.dayOfMonth < birth.dayOfMonth)
        ) {
            age--
        }
        age.coerceAtLeast(0)
    } catch (_: Exception) {
        0
    }
}
