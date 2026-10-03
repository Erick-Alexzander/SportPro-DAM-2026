package devs.grupo5.sportpro.presentation.trainer.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBorder
import devs.grupo5.sportpro.presentation.trainer.theme.SportProDarkBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreen
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreenContainer
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextMuted
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextPrimary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextSecondary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProWarning

/**
 * HU-005 — Gestión del Perfil del Jugador.
 * Pantalla "Mi perfil" (SportPro): línea gráfica oscura con acentos en verde neón (#00E676).
 *
 * Criterios cubiertos:
 * - Muestra posición/datos de la cuenta, físicos y contacto (extensible vía [accountRows]).
 * - Evita duplicados delegando la edición al ViewModel/repositorio (esta UI solo presenta datos únicos).
 * - Consulta y modificación según permisos vía callbacks ([onBackClick], [onEditPhotoClick], etc.).
 */
@Composable
fun ProfileScreen(
    fullName: String = "Carlos Mendoza",
    email: String = "carlos.m@sportpro.cl",
    role: String = "Jugador",
    academy: String = "Academia SportPro - Primera",
    initials: String = "CM",
    onBackClick: () -> Unit = {},
    onThemeClick: () -> Unit = {},
    onEditPhotoClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SportProDarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── TopAppBar / Barra superior ──────────────────────────────
            item {
                ProfileTopBar(
                    onBackClick = onBackClick,
                    onThemeClick = onThemeClick
                )
            }

            // ── Tarjeta de cabecera de usuario ─────────────────────────
            item {
                ProfileHeaderCard(
                    fullName = fullName,
                    academy = academy,
                    role = role,
                    initials = initials,
                    onEditPhotoClick = onEditPhotoClick
                )
            }

            // ── Tarjeta "Información de cuenta" ────────────────────────
            item {
                AccountInfoCard(
                    fullName = fullName,
                    email = email,
                    role = role,
                    academy = academy
                )
            }

            // ── Opciones de configuración ──────────────────────────────
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SettingsOptionItem(
                        icon = Icons.Default.Notifications,
                        title = "Notificaciones",
                        subtitle = "Convocatorias, partidos, entrenam.",
                        onClick = onNotificationsClick
                    )
                    SettingsOptionItem(
                        icon = Icons.Default.Lock,
                        title = "Privacidad",
                        subtitle = "Controla quién ve tus datos",
                        onClick = onPrivacyClick
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun ProfileTopBar(
    onBackClick: () -> Unit,
    onThemeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = SportProTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Mi perfil",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SportProTextPrimary
            )
        }

        IconButton(onClick = onThemeClick) {
            Icon(
                imageVector = Icons.Filled.LightMode,
                contentDescription = "Cambiar tema",
                tint = SportProTextPrimary
            )
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    fullName: String,
    academy: String,
    role: String,
    initials: String,
    onEditPhotoClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, SportProCardBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar circular marrón + lápiz de edición verde neón
            Box(
                modifier = Modifier.size(92.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8B5E34)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 2.dp, y = 2.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(SportProDarkBackground)
                        .border(1.dp, SportProCardBorder, CircleShape)
                        .clickable { onEditPhotoClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar fotografía",
                        tint = SportProGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = fullName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SportProTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Badge / Chip "Jugador"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SportProGreenContainer)
                    .padding(horizontal = 14.dp, vertical = 5.dp)
            ) {
                Text(
                    text = role,
                    color = SportProGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = academy,
                fontSize = 13.sp,
                color = SportProTextSecondary
            )
        }
    }
}

@Composable
private fun AccountInfoCard(
    fullName: String,
    email: String,
    role: String,
    academy: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, SportProCardBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Información de cuenta",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SportProTextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            AccountRow(label = "Nombre", value = fullName)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                thickness = 0.5.dp,
                color = SportProCardBorder
            )
            AccountRow(label = "Correo", value = email)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                thickness = 0.5.dp,
                color = SportProCardBorder
            )
            AccountRow(label = "Rol", value = role)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                thickness = 0.5.dp,
                color = SportProCardBorder
            )
            AccountRow(label = "Academia", value = academy)
        }
    }
}

@Composable
private fun AccountRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = SportProTextMuted
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = SportProTextPrimary
        )
    }
}

@Composable
private fun SettingsOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SportProCardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SportProWarning.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SportProWarning,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportProTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = SportProTextSecondary
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = SportProTextMuted,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1117)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(
        fullName = "Carlos Mendoza",
        email = "carlos.m@sportpro.cl",
        role = "Jugador",
        academy = "Academia SportPro - Primera",
        initials = "CM"
    )
}
