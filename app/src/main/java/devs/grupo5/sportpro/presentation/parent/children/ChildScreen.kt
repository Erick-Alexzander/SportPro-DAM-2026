package devs.grupo5.sportpro.presentation.parent.children

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import devs.grupo5.sportpro.data.model.GuardianProfile
import devs.grupo5.sportpro.data.model.LinkedChild
import devs.grupo5.sportpro.presentation.parent.components.ParentBadge
import devs.grupo5.sportpro.presentation.parent.components.ParentChildSelector
import devs.grupo5.sportpro.presentation.parent.components.ParentDetailDivider
import devs.grupo5.sportpro.presentation.parent.components.ParentDetailRow
import devs.grupo5.sportpro.presentation.parent.components.ParentEmptyState
import devs.grupo5.sportpro.presentation.parent.components.ParentMetricTile
import devs.grupo5.sportpro.presentation.parent.components.pressableClick
import devs.grupo5.sportpro.presentation.parent.theme.ParentBorder
import devs.grupo5.sportpro.presentation.parent.theme.ParentEmerald
import devs.grupo5.sportpro.presentation.parent.theme.ParentError
import devs.grupo5.sportpro.presentation.parent.theme.ParentGreenContainer
import devs.grupo5.sportpro.presentation.parent.theme.ParentNeonGreen
import devs.grupo5.sportpro.presentation.parent.theme.ParentStatusGold
import devs.grupo5.sportpro.presentation.parent.theme.ParentSurface
import devs.grupo5.sportpro.presentation.parent.theme.ParentSurfaceVariant
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextMuted
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextPrimary
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextSecondary

@Composable
fun ChildScreen(
    children: List<LinkedChild>,
    selectedChild: LinkedChild?,
    guardian: GuardianProfile,
    onSelectChild: (String) -> Unit = {},
    onRegisterChildClick: () -> Unit = {},
    onEditChildClick: (LinkedChild) -> Unit = {},
    onUnlinkChildClick: (LinkedChild) -> Unit = {},
    onEditPhysicalDataClick: (LinkedChild) -> Unit = {},
    onEditContactClick: (LinkedChild) -> Unit = {},
    onCreateAccessClick: (LinkedChild) -> Unit = {}
) {
    var childToUnlink by remember { mutableStateOf<LinkedChild?>(null) }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Mi hijo",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ParentTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ficha y rendimiento del menor vinculado a tu cuenta",
                fontSize = 13.sp,
                color = ParentTextSecondary
            )
        }

        if (children.isEmpty()) {
            item {
                ParentEmptyState(
                    icon = Icons.Default.FamilyRestroom,
                    title = "Sin jugador vinculado",
                    message = "Aún no registras a tu hijo menor. Puedes hacerlo desde el botón \"Registrar jugador menor\".",
                    actionLabel = "Registrar jugador menor",
                    onAction = onRegisterChildClick
                )
            }
        } else {
            val child = selectedChild ?: children.first()

            if (children.size > 1) {
                item {
                    ParentChildSelector(
                        children = children,
                        selectedChildId = child.id,
                        onSelect = onSelectChild
                    )
                }
            }

            item {
                ChildHeroCard(child = child)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onEditChildClick(child) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ParentNeonGreen,
                            contentColor = Color(0xFF00210B)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Editar datos",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = { childToUnlink = child },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ParentError.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = ParentError,
                            containerColor = ParentError.copy(alpha = 0.08f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.LinkOff,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Desvincular",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                GuardianResponsibleCard(guardian = guardian)
            }

            item {
                ChildPhysicalDataCard(
                    child = child,
                    onEditClick = { onEditPhysicalDataClick(child) }
                )
            }

            item {
                ChildContactCard(
                    child = child,
                    onEditClick = { onEditContactClick(child) },
                    onCall = { number -> context.dialPhone(number) },
                    onCopy = { number -> context.copyPhone(number) }
                )
            }

            item {
                Button(
                    onClick = { onCreateAccessClick(child) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ParentNeonGreen,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Crear acceso para el menor",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            item {
                OutlinedButton(
                    onClick = onRegisterChildClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ParentNeonGreen.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ParentNeonGreen,
                        containerColor = ParentGreenContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Vincular otro menor",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    childToUnlink?.let { child ->
        AlertDialog(
            onDismissRequest = { childToUnlink = null },
            containerColor = ParentSurface,
            titleContentColor = ParentTextPrimary,
            textContentColor = ParentTextSecondary,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "Desvincular jugador menor",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "${child.fullName} dejará de estar asociado a tu cuenta responsable y no podrás ver su rendimiento ni sus mensualidades.",
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        childToUnlink = null
                        onUnlinkChildClick(child)
                    }
                ) {
                    Text(
                        text = "Desvincular",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ParentError
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { childToUnlink = null }) {
                    Text(
                        text = "Cancelar",
                        fontSize = 14.sp,
                        color = ParentTextMuted
                    )
                }
            }
        )
    }
}

/**
 * Tarjeta con el apoderado responsable de la cuenta: nombre completo y relación con el menor.
 */
@Composable
private fun GuardianResponsibleCard(guardian: GuardianProfile) {
    ParentInfoCard(
        title = "Padre / Apoderado responsable",
        icon = Icons.Default.FamilyRestroom
    ) {
        ParentDetailDivider()
        Spacer(modifier = Modifier.height(12.dp))
        ParentDetailRow(
            label = "Responsable",
            value = guardian.fullName.ifBlank { "—" }
        )
        Spacer(modifier = Modifier.height(12.dp))
        ParentDetailDivider()
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Relación",
                fontSize = 13.sp,
                color = ParentTextMuted
            )
            Spacer(modifier = Modifier.size(16.dp))
            ParentBadge(text = guardian.title.label)
        }
    }
}

/**
 * Tarjeta con los datos físicos protegidos del menor (edad, fecha de nacimiento, talla y peso).
 */
@Composable
private fun ChildPhysicalDataCard(
    child: LinkedChild,
    onEditClick: () -> Unit
) {
    ParentInfoCard(
        title = "Datos físicos protegidos",
        icon = Icons.Default.Shield
    ) {
        ParentDetailDivider()
        Spacer(modifier = Modifier.height(12.dp))
        ParentDetailRow(label = "Edad", value = child.ageLabel)
        Spacer(modifier = Modifier.height(12.dp))
        ParentDetailDivider()
        Spacer(modifier = Modifier.height(12.dp))
        ParentDetailRow(label = "Fecha nac.", value = child.birthDateLabel)
        Spacer(modifier = Modifier.height(12.dp))
        ParentDetailDivider()
        Spacer(modifier = Modifier.height(12.dp))
        ParentDetailRow(label = "Talla", value = child.heightLabel)
        Spacer(modifier = Modifier.height(12.dp))
        ParentDetailDivider()
        Spacer(modifier = Modifier.height(12.dp))
        ParentDetailRow(label = "Peso", value = child.weightLabel)
        Spacer(modifier = Modifier.height(4.dp))
        ParentDetailDivider()
        Spacer(modifier = Modifier.height(4.dp))
        ParentCardAction(
            label = "Editar datos físicos",
            onClick = onEditClick
        )
    }
}

/**
 * Tarjeta con el contacto protegido del menor: teléfono principal y contacto de emergencia.
 */
@Composable
private fun ChildContactCard(
    child: LinkedChild,
    onEditClick: () -> Unit,
    onCall: (String) -> Unit,
    onCopy: (String) -> Unit
) {
    ParentInfoCard(
        title = "Contacto protegido",
        icon = Icons.Default.Call
    ) {
        ParentDetailDivider()
        Spacer(modifier = Modifier.height(12.dp))
        ProtectedContactRow(
            label = "Teléfono",
            number = child.phone,
            onCall = onCall,
            onCopy = onCopy,
            onEdit = onEditClick
        )
        Spacer(modifier = Modifier.height(12.dp))
        ParentDetailDivider()
        Spacer(modifier = Modifier.height(12.dp))
        ProtectedContactRow(
            label = "Emergencia",
            number = child.emergencyPhone,
            onCall = onCall,
            onCopy = onCopy,
            onEdit = onEditClick
        )
        Spacer(modifier = Modifier.height(4.dp))
        ParentDetailDivider()
        Spacer(modifier = Modifier.height(4.dp))
        ParentCardAction(
            label = "Editar contacto",
            onClick = onEditClick
        )
    }
}

/**
 * Fila de contacto: el número se puede llamar con un toque y el menú permite copiarlo o editarlo.
 */
@Composable
private fun ProtectedContactRow(
    label: String,
    number: String,
    onCall: (String) -> Unit,
    onCopy: (String) -> Unit,
    onEdit: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val hasNumber = number.isNotBlank()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = ParentTextMuted
        )
        Spacer(modifier = Modifier.size(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = number.ifBlank { "—" },
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (hasNumber) ParentEmerald else ParentTextMuted,
                modifier = Modifier.pressableClick(enabled = hasNumber) { onCall(number) }
            )
            Spacer(modifier = Modifier.size(4.dp))
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones de $label",
                        tint = ParentTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    containerColor = ParentSurfaceVariant
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Llamar",
                                fontSize = 13.sp,
                                color = ParentTextPrimary
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            if (hasNumber) onCall(number)
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Copiar número",
                                fontSize = 13.sp,
                                color = ParentTextPrimary
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            if (hasNumber) onCopy(number)
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Editar contacto",
                                fontSize = 13.sp,
                                color = ParentTextPrimary
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onEdit()
                        }
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta oscura reutilizable de la sección "Mi hijo".
 */
@Composable
private fun ParentInfoCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
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
                        .size(30.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(ParentGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = ParentNeonGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParentTextPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

/**
 * Acción discreta al pie de una tarjeta.
 */
@Composable
private fun ParentCardAction(
    label: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.height(36.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null,
            tint = ParentNeonGreen,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = ParentNeonGreen
        )
    }
}

@Composable
private fun ChildHeroCard(child: LinkedChild) {
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
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(ParentGreenContainer)
                        .border(2.dp, ParentNeonGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = child.initials,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ParentNeonGreen
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = child.fullName,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = ParentTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${child.category} · #${child.jerseyNumber} · ${child.position}",
                        fontSize = 13.sp,
                        color = ParentTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ParentStatusGold.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = child.status,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ParentStatusGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

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
                ParentMetricTile(value = child.stats.goals.toString(), label = "Goles", modifier = Modifier.weight(1f))
                ParentMetricTile(value = child.stats.assists.toString(), label = "Asist.", modifier = Modifier.weight(1f))
                ParentMetricTile(value = child.stats.matchesPlayed.toString(), label = "PJ", modifier = Modifier.weight(1f))
                ParentMetricTile(value = "${child.stats.attendancePercent}%", label = "Asist.%", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(14.dp))

            ParentDetailRow(label = "Categoría", value = child.category)
            Spacer(modifier = Modifier.height(8.dp))
            ParentDetailRow(label = "Edad", value = child.ageLabel)
            Spacer(modifier = Modifier.height(8.dp))
            ParentDetailRow(label = "Posición", value = child.position)
            Spacer(modifier = Modifier.height(8.dp))
            ParentDetailRow(label = "Número de camiseta", value = "#${child.jerseyNumber}")
            if (child.birthDateLabel != "—") {
                Spacer(modifier = Modifier.height(8.dp))
                ParentDetailRow(label = "Nacimiento", value = child.birthDateLabel)
            }
        }
    }
}

/**
 * Abre el marcador con el número indicado (`tel:`).
 */
private fun Context.dialPhone(number: String) {
    val uri = "tel:${number.filter { it.isDigit() || it == '+' }}".toUri()
    try {
        startActivity(Intent(Intent.ACTION_DIAL, uri))
    } catch (error: ActivityNotFoundException) {
        Toast.makeText(this, "No hay una aplicación de llamadas disponible", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Copia el número al portapapeles.
 */
private fun Context.copyPhone(number: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Teléfono", number))
    Toast.makeText(this, "Número copiado", Toast.LENGTH_SHORT).show()
}
