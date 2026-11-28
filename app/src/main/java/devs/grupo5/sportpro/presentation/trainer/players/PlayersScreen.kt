package devs.grupo5.sportpro.presentation.trainer.players

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import devs.grupo5.sportpro.data.model.Player
import devs.grupo5.sportpro.presentation.trainer.teams.CategoryChip
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBorder
import devs.grupo5.sportpro.presentation.trainer.theme.SportProDarkBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreen
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreenContainer
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextMuted
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextPrimary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextSecondary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProWarning

@Composable
fun PlayersScreen(
    viewModel: PlayerViewModel = viewModel(),
    onAddPlayerClick: () -> Unit = {}
) {
    val players by viewModel.players.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    val categories = listOf("Todos", "Primera", "Sub-15", "Sub-10")

    val filteredPlayers = players.filter { player ->
        val matchesCategory = selectedCategory == "Todos" || player.category == selectedCategory
        val matchesSearch = searchQuery.isBlank() ||
                player.fullName.contains(searchQuery, ignoreCase = true) ||
                player.position.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }
    var selectedPlayerId by remember { mutableStateOf<String?>(null) }
    val selectedPlayer = selectedPlayerId?.let { id -> players.find { it.id == id } }
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Jugadores",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${filteredPlayers.size} registrados",
                        fontSize = 14.sp,
                        color = SportProGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                FloatingActionButton(
                    onClick = onAddPlayerClick,
                    containerColor = SportProGreen,
                    contentColor = Color.Black,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Nuevo jugador")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Buscar jugador...", color = SportProTextMuted) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = SportProTextSecondary) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SportProCardBackground,
                    unfocusedContainerColor = SportProCardBackground,
                    focusedBorderColor = SportProGreen,
                    unfocusedBorderColor = SportProCardBorder,
                    focusedTextColor = SportProTextPrimary,
                    unfocusedTextColor = SportProTextPrimary
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Filter Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) SportProGreen else SportProCardBackground)
                            .border(
                                1.dp,
                                if (isSelected) SportProGreen else SportProCardBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { viewModel.setSelectedCategory(category) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) Color.Black else SportProTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Players List
            if (filteredPlayers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron jugadores.",
                        color = SportProTextMuted,
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredPlayers, key = { it.id }) { player ->
                        PlayerCard(
                            player = player,
                            onClick = { selectedPlayerId = player.id }
                        )
                    }
                }
            }
        }

        selectedPlayer?.let { player ->
            ManageGuardianDialog(
                player = player,
                onDismiss = { selectedPlayerId = null }
            )
        }
    }
}

@Composable
fun PlayerCard(player: Player, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, SportProCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SportProGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = player.position,
                        color = SportProGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = player.fullName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${player.category} • Camiseta #${player.jerseyNumber} • ${player.age} años",
                        fontSize = 12.sp,
                        color = SportProTextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (player.guardianUids.isEmpty()) "Sin apoderado vinculado" else "Apoderados vinculados: ${player.guardianUids.size}",
                        fontSize = 11.sp,
                        color = if (player.guardianUids.isEmpty()) SportProTextMuted else SportProGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            StatusChip(status = player.status)
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "activo" -> Pair(SportProGreenContainer, SportProGreen)
        "lesionado" -> Pair(SportProWarning.copy(alpha = 0.2f), SportProWarning)
        else -> Pair(SportProCardBorder, SportProTextMuted)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ManageGuardianDialog(
    player: Player,
    onDismiss: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val firestore = remember { FirebaseFirestore.getInstance() }

    fun linkGuardian() {
        val trimmedEmail = email.trim().lowercase()
        if (!trimmedEmail.contains("@")) return
        isLoading = true
        statusMessage = null
        isError = false
        scope.launch {
            try {
                val userQuery = firestore.collection("users")
                    .whereEqualTo("email", trimmedEmail)
                    .whereEqualTo("role", "PARENT")
                    .limit(1)
                    .get()
                    .await()
                val guardianDoc = userQuery.documents.firstOrNull()
                    ?: throw IllegalStateException("No se encontró un apoderado con ese correo.")
                val guardianUid = guardianDoc.id
                if (player.guardianUids.contains(guardianUid)) {
                    throw IllegalStateException("Este apoderado ya está vinculado al jugador.")
                }

                firestore.collection("players").document(player.id)
                    .update("guardianUids", FieldValue.arrayUnion(guardianUid))
                    .await()

                val parentAccessRef = firestore.collection("parentAccess").document(guardianUid)
                val accessData = mutableMapOf<String, Any>(
                    "parentUid" to guardianUid,
                    "playerIds" to FieldValue.arrayUnion(player.id)
                )
                val teamId = player.teamId
                if (!teamId.isNullOrBlank()) {
                    accessData["teamIds"] = FieldValue.arrayUnion(teamId)
                } else {
                    // Ensure teamIds exists on create; merge keeps existing on update
                    val existing = parentAccessRef.get().await()
                    if (!existing.exists()) accessData["teamIds"] = emptyList<String>()
                }
                parentAccessRef.set(accessData, SetOptions.merge()).await()

                statusMessage = "Apoderado vinculado correctamente."
                email = ""
            } catch (e: Exception) {
                isError = true
                statusMessage = e.message ?: "Ocurrió un error al vincular."
            } finally {
                isLoading = false
            }
        }
    }

    fun unlinkGuardian(guardianUid: String) {
        isLoading = true
        statusMessage = null
        isError = false
        scope.launch {
            try {
                firestore.collection("players").document(player.id)
                    .update("guardianUids", FieldValue.arrayRemove(guardianUid))
                    .await()

                val parentAccessRef = firestore.collection("parentAccess").document(guardianUid)
                val accessSnap = parentAccessRef.get().await()
                if (accessSnap.exists()) {
                    val currentPlayerIds = (accessSnap.get("playerIds") as? List<*>)?.filterIsInstance<String>().orEmpty()
                    val remainingPlayerIds = currentPlayerIds.filter { it != player.id }
                    val remainingTeamIds = if (remainingPlayerIds.isEmpty()) {
                        emptyList()
                    } else {
                        val teamIds = mutableSetOf<String>()
                        for (pid in remainingPlayerIds) {
                            val pSnap = firestore.collection("players").document(pid).get().await()
                            val tid = pSnap.getString("teamId")
                            if (!tid.isNullOrBlank()) teamIds.add(tid)
                        }
                        teamIds.toList()
                    }
                    parentAccessRef.set(
                        mapOf(
                            "parentUid" to guardianUid,
                            "playerIds" to remainingPlayerIds,
                            "teamIds" to remainingTeamIds
                        )
                    ).await()
                }

                statusMessage = "Apoderado desvinculado correctamente."
            } catch (e: Exception) {
                isError = true
                statusMessage = e.message ?: "Ocurrió un error al desvincular."
            } finally {
                isLoading = false
            }
        }
    }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        containerColor = SportProCardBackground,
        titleContentColor = SportProTextPrimary,
        textContentColor = SportProTextSecondary,
        title = {
            Text(
                text = "Apoderados: ${player.fullName}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Vincula un apoderado por correo. Solo cuentas con rol PARENT pueden vincularse.",
                    fontSize = 12.sp,
                    color = SportProTextSecondary
                )

                if (player.guardianUids.isEmpty()) {
                    Text(
                        text = "Este jugador no tiene apoderados vinculados.",
                        fontSize = 12.sp,
                        color = SportProTextMuted
                    )
                } else {
                    Text(
                        text = "Vinculados:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SportProTextPrimary
                    )
                    player.guardianUids.forEach { guardianUid ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = guardianUid,
                                fontSize = 11.sp,
                                color = SportProTextSecondary,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                enabled = !isLoading,
                                onClick = { unlinkGuardian(guardianUid) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LinkOff,
                                    contentDescription = "Desvincular",
                                    tint = SportProWarning,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Desvincular", color = SportProWarning, fontSize = 12.sp)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; statusMessage = null },
                    label = { Text("Correo del apoderado") },
                    placeholder = { Text("apoderado@ejemplo.com") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SportProDarkBackground,
                        unfocusedContainerColor = SportProDarkBackground,
                        focusedBorderColor = SportProGreen,
                        unfocusedBorderColor = SportProCardBorder,
                        focusedTextColor = SportProTextPrimary,
                        unfocusedTextColor = SportProTextPrimary,
                        focusedLabelColor = SportProGreen,
                        unfocusedLabelColor = SportProTextMuted
                    )
                )

                if (statusMessage != null) {
                    val color = if (isError) SportProWarning else SportProGreen
                    Text(
                        text = statusMessage!!,
                        fontSize = 12.sp,
                        color = color
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isLoading && email.contains("@"),
                onClick = { linkGuardian() },
                colors = ButtonDefaults.buttonColors(containerColor = SportProGreen, contentColor = Color.Black)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Vincular", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(enabled = !isLoading, onClick = onDismiss) {
                Text("Cerrar", color = SportProTextMuted)
            }
        }
    )
}
