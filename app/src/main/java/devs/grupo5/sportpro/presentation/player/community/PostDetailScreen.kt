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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.presentation.player.components.PlayerBottomNavigationBar
import devs.grupo5.sportpro.presentation.player.components.PlayerTab
import devs.grupo5.sportpro.presentation.player.theme.PlayerBackground
import devs.grupo5.sportpro.presentation.player.theme.PlayerBorder
import devs.grupo5.sportpro.presentation.player.theme.PlayerGreenContainer
import devs.grupo5.sportpro.presentation.player.theme.PlayerNeonGreen
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurface
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurfaceVariant
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextMuted
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextPrimary
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextSecondary
import devs.grupo5.sportpro.presentation.player.theme.SportProPlayerTheme

@Composable
fun PostDetailScreen(
    postId: String = "",
    currentUserInitials: String = "CM",
    onBackClick: () -> Unit = {},
    onTabSelected: (PlayerTab) -> Unit = {}
) {
    val post = findPostById(postId)
    var commentText by remember { mutableStateOf("") }

    SportProPlayerTheme {
        Scaffold(
            containerColor = PlayerBackground,
            bottomBar = {
                PlayerBottomNavigationBar(
                    currentTab = PlayerTab.COMMUNITY,
                    onTabSelected = onTabSelected
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Barra superior
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
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
                        text = "Publicación",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayerTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones de publicación",
                            tint = PlayerTextPrimary
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        MainPostCard(post = post)
                    }

                    item {
                        Text(
                            text = "${postComments.size} Comentarios",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlayerTextPrimary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    items(postComments, key = { it.id }) { comment ->
                        CommentCard(comment = comment)
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                HorizontalDivider(thickness = 0.5.dp, color = PlayerBorder)

                CommentInputBar(
                    initials = currentUserInitials,
                    value = commentText,
                    onValueChange = { commentText = it },
                    onSendClick = { commentText = "" }
                )
            }
        }
    }
}

@Composable
private fun MainPostCard(post: CommunityPost) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(
                    initials = post.initials,
                    tone = post.tone,
                    size = 44.dp,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.author,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayerTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = post.subtitle,
                        fontSize = 12.sp,
                        color = PlayerTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = post.text,
                fontSize = 14.sp,
                color = PlayerTextPrimary,
                lineHeight = 20.sp
            )

            if (post.hasImage) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(PlayerSurfaceVariant, PlayerGreenContainer)
                            )
                        )
                        .border(1.dp, PlayerBorder, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = "Imagen del partido",
                        tint = PlayerNeonGreen,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "💚 ${post.likes}", fontSize = 13.sp, color = PlayerTextSecondary)
                Spacer(modifier = Modifier.width(18.dp))
                Text(text = "💬 ${post.comments}", fontSize = 13.sp, color = PlayerTextSecondary)
            }
        }
    }
}

@Composable
private fun CommentCard(comment: PostComment) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Avatar(
            initials = comment.initials,
            tone = comment.tone,
            size = 38.dp,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = comment.author,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PlayerTextPrimary
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = comment.text,
                fontSize = 13.sp,
                color = PlayerTextSecondary,
                lineHeight = 19.sp
            )
            if (comment.meta != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = comment.meta,
                    fontSize = 11.sp,
                    color = PlayerTextMuted
                )
            }
        }
    }
}

@Composable
private fun CommentInputBar(
    initials: String,
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            initials = initials.take(2).uppercase(),
            tone = AvatarTone.GREEN,
            size = 38.dp,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.width(10.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = "Escribe un comentario...",
                    color = PlayerTextMuted,
                    fontSize = 13.sp
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            trailingIcon = {
                IconButton(
                    onClick = onSendClick,
                    enabled = value.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Enviar comentario",
                        tint = if (value.isNotBlank()) PlayerNeonGreen else PlayerTextMuted
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = PlayerSurface,
                unfocusedContainerColor = PlayerSurface,
                focusedBorderColor = PlayerNeonGreen,
                unfocusedBorderColor = PlayerBorder,
                focusedTextColor = PlayerTextPrimary,
                unfocusedTextColor = PlayerTextPrimary,
                cursorColor = PlayerNeonGreen,
                focusedPlaceholderColor = PlayerTextMuted,
                unfocusedPlaceholderColor = PlayerTextMuted
            )
        )
    }
}

@Composable
private fun Avatar(
    initials: String,
    tone: AvatarTone,
    size: Dp,
    fontSize: TextUnit
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(tone.backgroundColor()),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold,
            color = tone.contentColor()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF101413)
@Composable
private fun PostDetailScreenPreview() {
    PostDetailScreen()
}
