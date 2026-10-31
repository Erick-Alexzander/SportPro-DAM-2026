package devs.grupo5.sportpro.presentation.parent.profile

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.data.model.GuardianProfile
import devs.grupo5.sportpro.data.model.LinkedChild
import devs.grupo5.sportpro.data.model.MonthlyPayment
import devs.grupo5.sportpro.presentation.parent.components.ParentDetailRow
import devs.grupo5.sportpro.presentation.parent.components.ParentStatusPill
import devs.grupo5.sportpro.presentation.parent.components.pressableClick
import devs.grupo5.sportpro.presentation.parent.theme.ParentBorder
import devs.grupo5.sportpro.presentation.parent.theme.ParentError
import devs.grupo5.sportpro.presentation.parent.theme.ParentGreenContainer
import devs.grupo5.sportpro.presentation.parent.theme.ParentNeonGreen
import devs.grupo5.sportpro.presentation.parent.theme.ParentSurface
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextMuted
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextPrimary
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextSecondary

@Composable
fun ParentProfileScreen(
    guardian: GuardianProfile,
    children: List<LinkedChild> = emptyList(),
    payment: MonthlyPayment = MonthlyPayment(),
    unreadNotifications: Int = 0,
    onEditProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onPaymentClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Perfil",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ParentTextPrimary
            )
        }

        item {
            ProfileHeaderCard(
                guardian = guardian,
                onEditProfileClick = onEditProfileClick
            )
        }

        item {
            AccountInfoCard(
                guardian = guardian,
                children = children,
                payment = payment
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SettingsOptionItem(
                    icon = Icons.Default.Notifications,
                    title = "Notificaciones",
                    subtitle = if (unreadNotifications > 0) {
                        "$unreadNotifications sin leer"
                    } else {
                        "Convocatorias, comunicados y pagos"
                    },
                    onClick = onNotificationsClick
                )
                SettingsOptionItem(
                    icon = Icons.Default.Edit,
                    title = "Editar perfil",
                    subtitle = "Nombre, correo y forma de trato",
                    onClick = onEditProfileClick
                )
                SettingsOptionItem(
                    icon = Icons.Default.CreditCard,
                    title = "Mensualidad ${payment.period}",
                    subtitle = "Estado: ${payment.status.label}",
                    onClick = onPaymentClick
                )
                SettingsOptionItem(
                    icon = Icons.Default.Lock,
                    title = "Privacidad",
                    subtitle = "Datos del menor y responsables",
                    onClick = onPrivacyClick
                )
            }
        }

        item {
            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(percent = 50),
                border = BorderStroke(1.dp, ParentError.copy(alpha = 0.6f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ParentError,
                    containerColor = ParentError.copy(alpha = 0.08f)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Cerrar sesión",
                    tint = ParentError,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Cerrar sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParentError
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    guardian: GuardianProfile,
    onEditProfileClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, ParentBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = ParentSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(ParentGreenContainer)
                    .border(2.dp, ParentNeonGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = guardian.initials,
                    color = ParentNeonGreen,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = guardian.fullName.ifBlank { "Apoderado" },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ParentTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ParentGreenContainer)
                    .padding(horizontal = 14.dp, vertical = 5.dp)
            ) {
                Text(
                    text = guardian.roleLabel,
                    color = ParentNeonGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Toca tu avatar para editar el perfil",
                fontSize = 12.sp,
                color = ParentTextMuted,
                modifier = Modifier.pressableClick { onEditProfileClick() }
            )
        }
    }
}

@Composable
private fun AccountInfoCard(
    guardian: GuardianProfile,
    children: List<LinkedChild>,
    payment: MonthlyPayment
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, ParentBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = ParentSurface)
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
                color = ParentTextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            ParentDetailRow(label = "Nombre", value = guardian.fullName.ifBlank { "—" })
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                thickness = 0.5.dp,
                color = ParentBorder
            )
            ParentDetailRow(label = "Correo", value = guardian.email.ifBlank { "—" })
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                thickness = 0.5.dp,
                color = ParentBorder
            )
            ParentDetailRow(label = "Rol", value = guardian.title.label)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                thickness = 0.5.dp,
                color = ParentBorder
            )
            ParentDetailRow(
                label = "Menores vinculados",
                value = when (children.size) {
                    0 -> "Sin vincular"
                    1 -> "${children.first().fullName.ifBlank { "1 menor" }} (${children.first().parentRelation.ifBlank { "Responsable" }})"
                    else -> "${children.size} menores · ${children.joinToString(", ") { "${it.firstName} (${it.parentRelation.ifBlank { "Responsable" }})" }}"
                }
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                thickness = 0.5.dp,
                color = ParentBorder
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mensualidad ${payment.period}",
                    fontSize = 13.sp,
                    color = ParentTextMuted
                )
                ParentStatusPill(status = payment.status)
            }
        }
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
            .border(1.dp, ParentBorder, RoundedCornerShape(16.dp))
            .pressableClick { onClick() },
        colors = CardDefaults.cardColors(containerColor = ParentSurface)
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
                    .background(ParentGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ParentNeonGreen,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParentTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = ParentTextSecondary
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = ParentTextMuted,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
