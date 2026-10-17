package devs.grupo5.sportpro.presentation.player.community

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

private enum class CommunityTab(val title: String) {
    POSTS("Publicaciones"),
    CALL_UPS("Convocatorias abiertas")
}

@Composable
fun CommunityScreen(
    onCreatePostClick: () -> Unit = {},
    onPostClick: (String) -> Unit = {},
    onTabSelected: (PlayerTab) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(CommunityTab.POSTS) }
    var selectedFilter by remember { mutableStateOf("Todos") }

    val filteredCallUps = if (selectedFilter == "Todos") {
        openCallUps
    } else {
        openCallUps.filter { it.category == selectedFilter || it.position == selectedFilter }
    }

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
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Encabezado superior
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Comunidad",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayerTextPrimary
                    )
                    PublishPillButton(onClick = onCreatePostClick)
                }

                Spacer(modifier = Modifier.height(16.dp))

                CommunityTabRow(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    CommunityTab.POSTS -> PostsList(
                        onPostClick = onPostClick,
                        modifier = Modifier.weight(1f)
                    )
                    CommunityTab.CALL_UPS -> CallUpsList(
                        filters = callUpFilters,
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it },
                        callUps = filteredCallUps,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PublishPillButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(PlayerNeonGreen)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = Color(0xFF00210B),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Publicar",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00210B)
        )
    }
}

@Composable
private fun CommunityTabRow(
    selectedTab: CommunityTab,
    onTabSelected: (CommunityTab) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            CommunityTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = tab.title,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) PlayerNeonGreen else PlayerTextSecondary
                    )
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            CommunityTab.entries.forEach { tab ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(
                            if (tab == selectedTab) PlayerNeonGreen else PlayerBorder
                        )
                )
            }
        }
    }
}

@Composable
private fun PostsList(
    onPostClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(communityPosts, key = { it.id }) { post ->
            PostCard(post = post, onClick = { onPostClick(post.id) })
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun PostCard(
    post: CommunityPost,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(18.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(post.tone.backgroundColor()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.initials,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = post.tone.contentColor()
                    )
                }

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

                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Más opciones",
                        tint = PlayerTextMuted
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
                PostImagePlaceholder()
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer de interacción
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "💚 ${post.likes}", fontSize = 13.sp, color = PlayerTextSecondary)
                Spacer(modifier = Modifier.width(18.dp))
                Text(text = "💬 ${post.comments}", fontSize = 13.sp, color = PlayerTextSecondary)
                Spacer(modifier = Modifier.width(18.dp))
                Text(
                    text = "↗️ Compartir",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PlayerNeonGreen
                )
            }
        }
    }
}

@Composable
private fun PostImagePlaceholder() {
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

@Composable
private fun CallUpsList(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    callUps: List<OpenCallUp>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filters) { filter ->
                CallUpFilterChip(
                    label = filter,
                    isSelected = selectedFilter == filter,
                    onClick = { onFilterSelected(filter) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (callUps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay convocatorias para este filtro.",
                    color = PlayerTextMuted,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(callUps, key = { it.id }) { callUp ->
                    CallUpCard(callUp = callUp)
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun CallUpFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PlayerNeonGreen else PlayerSurface)
            .border(
                1.dp,
                if (isSelected) PlayerNeonGreen else PlayerBorder,
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else PlayerTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CallUpCard(callUp: OpenCallUp) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(16.dp))
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PlayerGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = null,
                        tint = PlayerNeonGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = callUp.club,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayerTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Convocatoria abierta",
                        fontSize = 12.sp,
                        color = PlayerTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Badge(text = callUp.category)
                Badge(text = callUp.position)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = callUp.description,
                fontSize = 13.sp,
                color = PlayerTextSecondary,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "📅 ${callUp.date}", fontSize = 12.sp, color = PlayerTextMuted)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "📍 ${callUp.location}", fontSize = 12.sp, color = PlayerTextMuted)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ver convocatoria",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlayerNeonGreen
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = PlayerNeonGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun Badge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(PlayerGreenContainer)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = PlayerNeonGreen
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF101413)
@Composable
private fun CommunityScreenPreview() {
    CommunityScreen()
}
