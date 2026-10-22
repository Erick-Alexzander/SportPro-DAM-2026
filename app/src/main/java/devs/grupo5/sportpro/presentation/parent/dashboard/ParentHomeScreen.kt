package devs.grupo5.sportpro.presentation.parent.dashboard

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.data.model.GuardianProfile
import devs.grupo5.sportpro.data.model.LinkedChild
import devs.grupo5.sportpro.data.model.MonthlyPayment
import devs.grupo5.sportpro.presentation.parent.components.ParentChildSelector
import devs.grupo5.sportpro.presentation.parent.components.ParentEmptyState
import devs.grupo5.sportpro.presentation.parent.components.ParentMetricTile
import devs.grupo5.sportpro.presentation.parent.components.ParentStatusPill
import devs.grupo5.sportpro.presentation.parent.components.pressableClick
import devs.grupo5.sportpro.presentation.parent.theme.ParentBackground
import devs.grupo5.sportpro.presentation.parent.theme.ParentBorder
import devs.grupo5.sportpro.presentation.parent.theme.ParentGreenContainer
import devs.grupo5.sportpro.presentation.parent.theme.ParentNeonGreen
import devs.grupo5.sportpro.presentation.parent.theme.ParentStatusGold
import devs.grupo5.sportpro.presentation.parent.theme.ParentSurface
import devs.grupo5.sportpro.presentation.parent.theme.ParentSurfaceVariant
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextMuted
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextPrimary
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextSecondary

@Composable
fun ParentHomeScreen(
    guardian: GuardianProfile,
    child: LinkedChild?,
    children: List<LinkedChild> = emptyList(),
    payment: MonthlyPayment,
    unreadNotifications: Int = 0,
    onNotificationsClick: () -> Unit = {},
    onRegisterChildClick: () -> Unit = {},
    onSelectChild: (String) -> Unit = {},
    onPaymentClick: () -> Unit = {},
    onChildTabClick: () -> Unit = {},
    onCalendarTabClick: () -> Unit = {},
    onAnnouncementsTabClick: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            ParentHeader(
                guardian = guardian,
                unreadNotifications = unreadNotifications,
                onNotificationsClick = onNotificationsClick
            )
        }

        if (children.size > 1) {
            item {
                ParentChildSelector(
                    children = children,
                    selectedChildId = child?.id,
                    onSelect = onSelectChild
                )
            }
        }

        item {
            LinkedChildCard(
                child = child,
                onRegisterChildClick = onRegisterChildClick
            )
        }

        item {
            RegisterChildBanner(
                childrenCount = children.size,
                onClick = onRegisterChildClick
            )
        }

        item {
            PaymentStatusCard(
                payment = payment,
                onClick = onPaymentClick
            )
        }

        item {
            Text(
                text = "Accesos rápidos",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ParentTextPrimary
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickAccessCard(
                    title = "Mi hijo",
                    icon = Icons.Default.FamilyRestroom,
                    onClick = onChildTabClick,
                    modifier = Modifier.weight(1f)
                )
                QuickAccessCard(
                    title = "Calendario",
                    icon = Icons.Default.CalendarMonth,
                    onClick = onCalendarTabClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickAccessCard(
                    title = "Mensualid.",
                    icon = Icons.Default.CreditCard,
                    onClick = onPaymentClick,
                    modifier = Modifier.weight(1f)
                )
                QuickAccessCard(
                    title = "Anuncios",
                    icon = Icons.Default.Campaign,
                    onClick = onAnnouncementsTabClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ParentHeader(
    guardian: GuardianProfile,
    unreadNotifications: Int,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = guardian.title.greeting,
                fontSize = 14.sp,
                color = ParentTextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = guardian.firstName.ifBlank { "Apoderado" },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ParentTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = guardian.roleLabel,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = ParentNeonGreen
            )
        }

        Box {
            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(ParentSurface)
                    .border(1.dp, ParentBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificaciones",
                    tint = ParentTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (unreadNotifications > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(ParentNeonGreen)
                        .border(2.dp, ParentBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = unreadNotifications.coerceAtMost(9).toString(),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00210B)
                    )
                }
            }
        }
    }
}

@Composable
private fun LinkedChildCard(
    child: LinkedChild?,
    onRegisterChildClick: () -> Unit
) {
    if (child == null) {
        ParentEmptyState(
            icon = Icons.Default.FamilyRestroom,
            title = "Sin jugador vinculado",
            message = "Registra a tu hijo menor para seguir su progreso, sus goles y la mensualidad de la academia.",
            actionLabel = "Registrar jugador menor",
            onAction = onRegisterChildClick
        )
        return
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, ParentBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = ParentSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(ParentGreenContainer)
                        .border(2.dp, ParentNeonGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = child.initials,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ParentNeonGreen
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = child.fullName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ParentTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = child.category,
                            fontSize = 13.sp,
                            color = ParentTextSecondary
                        )
                        Text(
                            text = " · ",
                            fontSize = 13.sp,
                            color = ParentTextMuted
                        )
                        Text(
                            text = "#${child.jerseyNumber}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ParentNeonGreen
                        )
                        Text(
                            text = " · ",
                            fontSize = 13.sp,
                            color = ParentTextMuted
                        )
                        Text(
                            text = child.position,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ParentNeonGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ParentSurfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = ParentStatusGold,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Menor de edad · Datos protegidos",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ParentStatusGold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ParentMetricTile(
                    value = child.stats.goals.toString(),
                    label = "Goles",
                    modifier = Modifier.weight(1f)
                )
                ParentMetricTile(
                    value = child.stats.assists.toString(),
                    label = "Asist.",
                    modifier = Modifier.weight(1f)
                )
                ParentMetricTile(
                    value = child.stats.matchesPlayed.toString(),
                    label = "PJ",
                    modifier = Modifier.weight(1f)
                )
                ParentMetricTile(
                    value = "${child.stats.attendancePercent}%",
                    label = "Asist.%",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun RegisterChildBanner(
    childrenCount: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ParentGreenContainer)
            .border(1.5.dp, ParentNeonGreen, RoundedCornerShape(18.dp))
            .pressableClick { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (childrenCount > 0) "Vincular otro menor" else "Registrar jugador menor",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ParentTextPrimary
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Quedará vinculado a tu cuenta responsable",
                fontSize = 12.sp,
                color = ParentTextSecondary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(ParentNeonGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Registrar jugador menor",
                tint = Color(0xFF00210B),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun PaymentStatusCard(
    payment: MonthlyPayment,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, ParentBorder, RoundedCornerShape(18.dp))
            .pressableClick { onClick() },
        colors = CardDefaults.cardColors(containerColor = ParentSurface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ParentSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = ParentNeonGreen,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Mensualidad ${payment.period}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParentTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "SportPro no procesa pagos · Solo registro de estado",
                    fontSize = 11.sp,
                    color = ParentTextMuted,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            ParentStatusPill(status = payment.status)
        }
    }
}

@Composable
private fun QuickAccessCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, ParentBorder, RoundedCornerShape(16.dp))
            .pressableClick { onClick() },
        colors = CardDefaults.cardColors(containerColor = ParentSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
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
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ParentTextPrimary
            )
        }
    }
}
