package devs.grupo5.sportpro.presentation.parent.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.data.model.ChildStats
import devs.grupo5.sportpro.data.model.GuardianProfile
import devs.grupo5.sportpro.data.model.GuardianTitle
import devs.grupo5.sportpro.data.model.LinkedChild
import devs.grupo5.sportpro.data.model.MonthlyPayment
import devs.grupo5.sportpro.data.model.ParentNotification
import devs.grupo5.sportpro.data.model.PaymentStatus
import devs.grupo5.sportpro.data.model.isValidBirthDate
import devs.grupo5.sportpro.data.model.normalizeBirthDate
import devs.grupo5.sportpro.presentation.parent.components.ParentMetricTile
import devs.grupo5.sportpro.presentation.parent.components.ParentStatusPill
import devs.grupo5.sportpro.presentation.parent.theme.ParentBorder
import devs.grupo5.sportpro.presentation.parent.theme.ParentEmerald
import devs.grupo5.sportpro.presentation.parent.theme.ParentGreenContainer
import devs.grupo5.sportpro.presentation.parent.theme.ParentNeonGreen
import devs.grupo5.sportpro.presentation.parent.theme.ParentStatusBrown
import devs.grupo5.sportpro.presentation.parent.theme.ParentSurface
import devs.grupo5.sportpro.presentation.parent.theme.ParentSurfaceVariant
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextMuted
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextPrimary
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextSecondary
import devs.grupo5.sportpro.presentation.parent.theme.ParentWarning

private val childCategories = listOf("Primera", "Sub-15", "Sub-10")
private val childPositions = listOf("POR", "DEF", "MED", "DEL")

/**
 * Modal de registro / edición del menor vinculado a la cuenta del apoderado.
 */
@Composable
fun RegisterChildDialog(
    initialChild: LinkedChild? = null,
    onDismiss: () -> Unit = {},
    onSave: (LinkedChild) -> Unit = {}
) {
    var firstName by remember { mutableStateOf(initialChild?.firstName.orEmpty()) }
    var lastName by remember { mutableStateOf(initialChild?.lastName.orEmpty()) }
    var category by remember { mutableStateOf(initialChild?.category ?: "Sub-15") }
    var age by remember { mutableStateOf(initialChild?.age?.takeIf { it > 0 }?.toString().orEmpty()) }
    var jerseyNumber by remember { mutableStateOf(initialChild?.jerseyNumber?.takeIf { it > 0 }?.toString().orEmpty()) }
    var position by remember { mutableStateOf(initialChild?.position ?: "DEL") }
    var showErrors by remember { mutableStateOf(false) }

    val isEditing = initialChild != null
    val isValid = firstName.isNotBlank() && lastName.isNotBlank() &&
        (jerseyNumber.toIntOrNull() ?: 0) in 1..99

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ParentSurface,
        titleContentColor = ParentTextPrimary,
        textContentColor = ParentTextSecondary,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = if (isEditing) "Editar jugador menor" else "Registrar jugador menor",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ParentDialogField(
                    label = "NOMBRE",
                    value = firstName,
                    onValueChange = { firstName = it },
                    placeholder = "Felipe",
                    isError = showErrors && firstName.isBlank()
                )
                ParentDialogField(
                    label = "APELLIDO",
                    value = lastName,
                    onValueChange = { lastName = it },
                    placeholder = "Muñoz",
                    isError = showErrors && lastName.isBlank()
                )

                Text(
                    text = "CATEGORÍA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    color = ParentNeonGreen
                )
                ParentOptionRow(
                    options = childCategories,
                    selected = category,
                    onSelected = { category = it }
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ParentDialogField(
                        label = "EDAD",
                        value = age,
                        onValueChange = { age = it.filter(Char::isDigit).take(2) },
                        placeholder = "15",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    )
                    ParentDialogField(
                        label = "N° CAMISETA",
                        value = jerseyNumber,
                        onValueChange = { jerseyNumber = it.filter(Char::isDigit).take(2) },
                        placeholder = "11",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number,
                        isError = showErrors && (jerseyNumber.toIntOrNull() ?: 0) !in 1..99
                    )
                }

                Text(
                    text = "POSICIÓN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    color = ParentNeonGreen
                )
                ParentOptionRow(
                    options = childPositions,
                    selected = position,
                    onSelected = { position = it }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ParentGreenContainer)
                        .border(1.dp, ParentNeonGreen.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = ParentNeonGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Los datos del menor quedan protegidos y solo son visibles por el administrador y el apoderado responsable.",
                        fontSize = 11.sp,
                        color = ParentTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    showErrors = true
                    if (isValid) {
                        onSave(
                            (initialChild ?: LinkedChild(id = "lc_${System.currentTimeMillis()}")).copy(
                                firstName = firstName.trim(),
                                lastName = lastName.trim(),
                                category = category,
                                age = age.toIntOrNull() ?: 0,
                                jerseyNumber = jerseyNumber.toIntOrNull() ?: 0,
                                position = position
                            )
                        )
                    }
                }
            ) {
                Text(
                    text = if (isEditing) "Guardar cambios" else "Registrar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParentNeonGreen
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancelar",
                    fontSize = 14.sp,
                    color = ParentTextMuted
                )
            }
        }
    )
}

/**
 * Modal de edición del perfil del apoderado (nombre, correo y forma de trato).
 */
@Composable
fun EditProfileDialog(
    profile: GuardianProfile,
    onDismiss: () -> Unit = {},
    onSave: (GuardianProfile) -> Unit = {}
) {
    var firstName by remember { mutableStateOf(profile.firstName) }
    var lastName by remember { mutableStateOf(profile.lastName) }
    var email by remember { mutableStateOf(profile.email) }
    var title by remember { mutableStateOf(profile.title) }
    var showErrors by remember { mutableStateOf(false) }

    val isValid = firstName.isNotBlank() && lastName.isNotBlank() && email.contains("@")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ParentSurface,
        titleContentColor = ParentTextPrimary,
        textContentColor = ParentTextSecondary,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Editar perfil",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ParentDialogField(
                    label = "NOMBRE",
                    value = firstName,
                    onValueChange = { firstName = it },
                    placeholder = "María",
                    isError = showErrors && firstName.isBlank()
                )
                ParentDialogField(
                    label = "APELLIDO",
                    value = lastName,
                    onValueChange = { lastName = it },
                    placeholder = "González",
                    isError = showErrors && lastName.isBlank()
                )
                ParentDialogField(
                    label = "CORREO ELECTRÓNICO",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "correo@ejemplo.com",
                    keyboardType = KeyboardType.Email,
                    isError = showErrors && !email.contains("@")
                )

                Text(
                    text = "ROL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    color = ParentNeonGreen
                )
                ParentOptionRow(
                    options = GuardianTitle.entries.map { it.label },
                    selected = title.label,
                    onSelected = { label ->
                        title = GuardianTitle.entries.first { it.label == label }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    showErrors = true
                    if (isValid) {
                        onSave(
                            profile.copy(
                                firstName = firstName.trim(),
                                lastName = lastName.trim(),
                                email = email.trim(),
                                title = title
                            )
                        )
                    }
                }
            ) {
                Text(
                    text = "Guardar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParentNeonGreen
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancelar",
                    fontSize = 14.sp,
                    color = ParentTextMuted
                )
            }
        }
    )
}

/**
 * Modal genérico de información (detalle de evento, avisos de privacidad).
 */
@Composable
fun SimpleInfoDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ParentSurface,
        titleContentColor = ParentTextPrimary,
        textContentColor = ParentTextSecondary,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = message,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Entendido",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParentNeonGreen
                )
            }
        }
    )
}

/**
 * Modal con los avisos recibidos por el apoderado.
 */
@Composable
fun NotificationsDialog(
    notifications: List<ParentNotification>,
    onDismiss: () -> Unit = {},
    onMarkAsRead: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ParentSurface,
        titleContentColor = ParentTextPrimary,
        textContentColor = ParentTextSecondary,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = null,
                    tint = ParentNeonGreen,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Notificaciones",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (notifications.isEmpty()) {
                    Text(
                        text = "No tienes notificaciones por ahora.",
                        fontSize = 13.sp,
                        color = ParentTextMuted
                    )
                } else {
                    notifications.forEach { notification ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (notification.isRead) ParentSurfaceVariant
                                    else ParentGreenContainer
                                )
                                .border(
                                    1.dp,
                                    if (notification.isRead) ParentBorder
                                    else ParentNeonGreen.copy(alpha = 0.35f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        if (notification.isRead) ParentTextMuted else ParentNeonGreen
                                    )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = notification.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ParentTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = notification.body,
                                    fontSize = 12.sp,
                                    color = ParentTextSecondary,
                                    lineHeight = 17.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = notification.time,
                                    fontSize = 11.sp,
                                    color = ParentTextMuted
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onMarkAsRead) {
                Text(
                    text = "Marcar como leídas",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParentNeonGreen
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cerrar",
                    fontSize = 14.sp,
                    color = ParentTextMuted
                )
            }
        }
    )
}

/**
 * Modal de estado de la mensualidad. SportPro no procesa pagos: solo registra el estado.
 */
@Composable
fun PaymentStatusDialog(
    payment: MonthlyPayment,
    onDismiss: () -> Unit = {},
    onStatusChange: (PaymentStatus) -> Unit = {},
    onUploadReceipt: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ParentSurface,
        titleContentColor = ParentTextPrimary,
        textContentColor = ParentTextSecondary,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = ParentNeonGreen,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Mensualidad ${payment.period}",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ParentStatusBrown)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Estado actual",
                        fontSize = 13.sp,
                        color = ParentTextSecondary
                    )
                    ParentStatusPill(status = payment.status)
                }

                Text(
                    text = "SportPro no procesa pagos · Solo registro de estado",
                    fontSize = 12.sp,
                    color = ParentTextMuted,
                    lineHeight = 17.sp
                )

                Text(
                    text = "Actualiza el estado según el comprobante que presentaste en la academia.",
                    fontSize = 12.sp,
                    color = ParentTextSecondary,
                    lineHeight = 17.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PaymentChoiceButton(
                        label = "Pagado",
                        selected = payment.status == PaymentStatus.PAGADO,
                        selectedColor = ParentEmerald,
                        onClick = { onStatusChange(PaymentStatus.PAGADO) },
                        modifier = Modifier.weight(1f)
                    )
                    PaymentChoiceButton(
                        label = "Pendiente",
                        selected = payment.status == PaymentStatus.PENDIENTE,
                        selectedColor = ParentWarning,
                        onClick = { onStatusChange(PaymentStatus.PENDIENTE) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Button(
                    onClick = onUploadReceipt,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (payment.receiptRegistered) ParentGreenContainer else ParentSurfaceVariant,
                        contentColor = if (payment.receiptRegistered) ParentNeonGreen else ParentTextSecondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (payment.receiptRegistered) "Comprobante registrado" else "Registrar comprobante",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cerrar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParentNeonGreen
                )
            }
        }
    )
}

/**
 * Modal de resumen estadístico del menor (usado por el botón flotante).
 */
@Composable
fun ChildStatsDialog(
    child: LinkedChild?,
    onDismiss: () -> Unit = {}
) {
    val stats = child?.stats ?: ChildStats()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ParentSurface,
        titleContentColor = ParentTextPrimary,
        textContentColor = ParentTextSecondary,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = if (child != null) "Rendimiento · ${child.fullName}" else "Rendimiento",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (child == null) {
                    Text(
                        text = "Registra a tu hijo menor para visualizar sus métricas deportivas.",
                        fontSize = 13.sp,
                        color = ParentTextSecondary
                    )
                } else {
                    Text(
                        text = "${child.category} · #${child.jerseyNumber} · ${child.position}",
                        fontSize = 13.sp,
                        color = ParentTextMuted
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ParentMetricTile(value = stats.goals.toString(), label = "Goles", modifier = Modifier.weight(1f))
                        ParentMetricTile(value = stats.assists.toString(), label = "Asist.", modifier = Modifier.weight(1f))
                        ParentMetricTile(value = stats.matchesPlayed.toString(), label = "PJ", modifier = Modifier.weight(1f))
                        ParentMetricTile(value = "${stats.attendancePercent}%", label = "Asist.%", modifier = Modifier.weight(1f))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cerrar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParentNeonGreen
                )
            }
        }
    )
}

/**
 * Modal de edición de los datos físicos protegidos del menor (edad, fecha de nacimiento,
 * talla y peso). Los cambios se reflejan en tiempo real en la tarjeta "Datos físicos protegidos".
 */
@Composable
fun ChildPhysicalDataDialog(
    child: LinkedChild,
    onDismiss: () -> Unit = {},
    onSave: (LinkedChild) -> Unit = {}
) {
    var age by remember { mutableStateOf(child.age.takeIf { it > 0 }?.toString().orEmpty()) }
    var birthDate by remember { mutableStateOf(child.birthDateIso) }
    var height by remember { mutableStateOf(child.heightCm.takeIf { it > 0 }?.toString().orEmpty()) }
    var weight by remember { mutableStateOf(child.weightKg.takeIf { it > 0 }?.toString().orEmpty()) }
    var showErrors by remember { mutableStateOf(false) }

    val ageValue = age.toIntOrNull() ?: 0
    val heightValue = height.toIntOrNull() ?: 0
    val weightValue = weight.toIntOrNull() ?: 0
    val isValid = ageValue in 1..25 &&
        heightValue in 80..250 &&
        weightValue in 20..200 &&
        birthDate.isValidBirthDate()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ParentSurface,
        titleContentColor = ParentTextPrimary,
        textContentColor = ParentTextSecondary,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Editar datos físicos",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ParentDialogField(
                        label = "EDAD",
                        value = age,
                        onValueChange = { age = it.filter(Char::isDigit).take(2) },
                        placeholder = "14",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number,
                        isError = showErrors && ageValue !in 1..25
                    )
                    ParentDialogField(
                        label = "FECHA NAC.",
                        value = birthDate,
                        onValueChange = { birthDate = it.take(10) },
                        placeholder = "2010-05-18",
                        modifier = Modifier.weight(1f),
                        isError = showErrors && !birthDate.isValidBirthDate()
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ParentDialogField(
                        label = "TALLA (CM)",
                        value = height,
                        onValueChange = { height = it.filter(Char::isDigit).take(3) },
                        placeholder = "162",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number,
                        isError = showErrors && heightValue !in 80..250
                    )
                    ParentDialogField(
                        label = "PESO (KG)",
                        value = weight,
                        onValueChange = { weight = it.filter(Char::isDigit).take(3) },
                        placeholder = "55",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number,
                        isError = showErrors && weightValue !in 20..200
                    )
                }

                Text(
                    text = "La fecha de nacimiento admite el formato aaaa-mm-dd o dd/mm/aaaa.",
                    fontSize = 11.sp,
                    color = ParentTextMuted
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ParentGreenContainer)
                        .border(1.dp, ParentNeonGreen.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = ParentNeonGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Estos datos son protegidos: solo el administrador de la academia y el apoderado responsable pueden consultarlos.",
                        fontSize = 11.sp,
                        color = ParentTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    showErrors = true
                    if (isValid) {
                        onSave(
                            child.copy(
                                age = ageValue,
                                birthDate = birthDate.normalizeBirthDate(),
                                heightCm = heightValue,
                                weightKg = weightValue
                            )
                        )
                    }
                }
            ) {
                Text(
                    text = "Guardar cambios",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParentNeonGreen
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancelar",
                    fontSize = 14.sp,
                    color = ParentTextMuted
                )
            }
        }
    )
}

/**
 * Modal de edición del contacto protegido del menor (teléfono principal y contacto de emergencia).
 */
@Composable
fun ChildContactDialog(
    child: LinkedChild,
    onDismiss: () -> Unit = {},
    onSave: (LinkedChild) -> Unit = {}
) {
    var phone by remember { mutableStateOf(child.phone) }
    var emergencyPhone by remember { mutableStateOf(child.emergencyPhone) }
    var showErrors by remember { mutableStateOf(false) }

    fun digitsOf(value: String): Int = value.count { it.isDigit() }

    val isValid = digitsOf(phone) >= 7 && digitsOf(emergencyPhone) >= 7

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ParentSurface,
        titleContentColor = ParentTextPrimary,
        textContentColor = ParentTextSecondary,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Editar contacto",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ParentDialogField(
                    label = "TELÉFONO",
                    value = phone,
                    onValueChange = { phone = it.take(20) },
                    placeholder = "+56 9 4321 0987",
                    keyboardType = KeyboardType.Phone,
                    isError = showErrors && digitsOf(phone) < 7
                )
                ParentDialogField(
                    label = "CONTACTO DE EMERGENCIA",
                    value = emergencyPhone,
                    onValueChange = { emergencyPhone = it.take(20) },
                    placeholder = "+56 9 5555 0187",
                    keyboardType = KeyboardType.Phone,
                    isError = showErrors && digitsOf(emergencyPhone) < 7
                )

                Text(
                    text = "Desde el panel puedes llamar a estos números o copiarlos con un toque.",
                    fontSize = 11.sp,
                    color = ParentTextMuted
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    showErrors = true
                    if (isValid) {
                        onSave(
                            child.copy(
                                phone = phone.trim(),
                                emergencyPhone = emergencyPhone.trim()
                            )
                        )
                    }
                }
            ) {
                Text(
                    text = "Guardar cambios",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParentNeonGreen
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancelar",
                    fontSize = 14.sp,
                    color = ParentTextMuted
                )
            }
        }
    )
}

@Composable
private fun PaymentChoiceButton(
    label: String,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) selectedColor.copy(alpha = 0.15f) else ParentSurfaceVariant)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) selectedColor else ParentBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) selectedColor else ParentTextSecondary
        )
    }
}

@Composable
private fun ParentDialogField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = ParentNeonGreen
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = isError,
            placeholder = {
                Text(text = placeholder, fontSize = 13.sp, color = ParentTextMuted)
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = ParentTextPrimary,
                unfocusedTextColor = ParentTextPrimary,
                focusedBorderColor = ParentNeonGreen,
                unfocusedBorderColor = ParentBorder,
                focusedContainerColor = ParentSurfaceVariant,
                unfocusedContainerColor = ParentSurfaceVariant,
                cursorColor = ParentNeonGreen,
                errorBorderColor = ParentWarning
            )
        )
    }
}

@Composable
private fun ParentOptionRow(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) ParentGreenContainer else ParentSurfaceVariant)
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) ParentNeonGreen else ParentBorder,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelected(option) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) ParentNeonGreen else ParentTextSecondary
                )
            }
        }
    }
}
