package devs.grupo5.sportpro.presentation.trainer.announcements

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import devs.grupo5.sportpro.data.model.AnnouncementAudience
import devs.grupo5.sportpro.data.model.ParentAnnouncement
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBorder
import devs.grupo5.sportpro.presentation.trainer.theme.SportProDarkBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProError
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreen
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextMuted
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextPrimary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextSecondary
import java.time.format.DateTimeFormatter

@Composable
fun TrainerAnnouncementsScreen(
    viewModel: TrainerAnnouncementsViewModel = viewModel(),
    authorName: String = "Entrenador",
    onBackClick: () -> Unit = {}
) {
    val announcements by viewModel.announcements.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val actionError by viewModel.actionError.collectAsState()
    val syncError by viewModel.syncError.collectAsState()

    var showEditor by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<ParentAnnouncement?>(null) }
    var deleteTarget by remember { mutableStateOf<ParentAnnouncement?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SportProDarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = SportProTextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Comunicados", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = SportProTextPrimary)
                    Text("Publica novedades para apoderados", fontSize = 13.sp, color = SportProTextSecondary)
                }
            }

            val errorText = actionError ?: syncError
            if (!errorText.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorText, color = SportProError, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (announcements.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay comunicados publicados.", color = SportProTextMuted)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(announcements, key = { it.id }) { announcement ->
                        AnnouncementCard(
                            announcement = announcement,
                            onClick = {
                                editing = announcement
                                showEditor = true
                            },
                            onDelete = { deleteTarget = announcement }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                editing = null
                viewModel.clearActionError()
                showEditor = true
            },
            containerColor = SportProGreen,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Nuevo comunicado")
        }
    }

    if (showEditor) {
        AnnouncementEditorDialog(
            initial = editing,
            isSaving = isSaving,
            error = actionError,
            onDismiss = {
                if (!isSaving) {
                    showEditor = false
                    viewModel.clearActionError()
                }
            },
            onSave = { title, body, audience, pinned ->
                viewModel.saveAnnouncement(
                    editingId = editing?.id,
                    title = title,
                    body = body,
                    audience = audience,
                    authorName = authorName,
                    teamId = editing?.teamId,
                    pinned = pinned,
                    onSuccess = { showEditor = false }
                )
            }
        )
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            containerColor = SportProCardBackground,
            title = { Text("Eliminar comunicado", color = SportProTextPrimary) },
            text = { Text("¿Eliminar \"${target.title}\"? Esta acción no se puede deshacer.", color = SportProTextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAnnouncement(target.id)
                    deleteTarget = null
                }) {
                    Text("Eliminar", color = SportProError)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text("Cancelar", color = SportProTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun AnnouncementCard(
    announcement: ParentAnnouncement,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateText = remember(announcement.createdAt) {
        runCatching { announcement.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) }.getOrDefault("")
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SportProCardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (announcement.pinned) {
                            Icon(Icons.Default.PushPin, contentDescription = null, tint = SportProGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            announcement.title.ifBlank { "(Sin título)" },
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = SportProTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "${announcement.audience.value} · $dateText",
                        fontSize = 12.sp,
                        color = SportProTextSecondary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = SportProError)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                announcement.body,
                fontSize = 14.sp,
                color = SportProTextPrimary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AnnouncementEditorDialog(
    initial: ParentAnnouncement?,
    isSaving: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSave: (title: String, body: String, audience: AnnouncementAudience, pinned: Boolean) -> Unit
) {
    var title by remember(initial?.id) { mutableStateOf(initial?.title.orEmpty()) }
    var body by remember(initial?.id) { mutableStateOf(initial?.body.orEmpty()) }
    var audience by remember(initial?.id) { mutableStateOf(initial?.audience ?: AnnouncementAudience.APODERADOS) }
    var pinned by remember(initial?.id) { mutableStateOf(initial?.pinned ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SportProCardBackground,
        title = {
            Text(
                if (initial == null) "Nuevo comunicado" else "Editar comunicado",
                color = SportProTextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                if (!error.isNullOrBlank()) {
                    Text(error, color = SportProError, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título", color = SportProTextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = announcementFieldColors()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text("Mensaje", color = SportProTextSecondary) },
                    minLines = 4,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth(),
                    colors = announcementFieldColors()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Audiencia", color = SportProTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(AnnouncementAudience.TODOS, AnnouncementAudience.APODERADOS, AnnouncementAudience.JUGADORES).forEach { option ->
                        val selected = audience == option
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selected) SportProGreen else Color.Transparent)
                                .border(1.dp, if (selected) SportProGreen else SportProCardBorder, RoundedCornerShape(20.dp))
                                .clickable { audience = option }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                option.value.lowercase().replaceFirstChar { it.uppercase() },
                                color = if (selected) Color.Black else SportProTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Fijar comunicado", color = SportProTextPrimary, modifier = Modifier.weight(1f))
                    Switch(
                        checked = pinned,
                        onCheckedChange = { pinned = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = SportProGreen)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, body, audience, pinned) },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = SportProGreen, contentColor = Color.Black)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.Black)
                } else {
                    Text(if (initial == null) "Publicar" else "Guardar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text("Cancelar", color = SportProTextSecondary)
            }
        }
    )
}

@Composable
private fun announcementFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = SportProDarkBackground,
    unfocusedContainerColor = SportProDarkBackground,
    focusedBorderColor = SportProGreen,
    unfocusedBorderColor = SportProCardBorder,
    focusedTextColor = SportProTextPrimary,
    unfocusedTextColor = SportProTextPrimary,
    cursorColor = SportProGreen
)
