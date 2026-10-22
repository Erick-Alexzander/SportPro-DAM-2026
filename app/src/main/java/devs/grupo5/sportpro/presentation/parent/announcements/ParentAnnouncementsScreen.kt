package devs.grupo5.sportpro.presentation.parent.announcements

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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.presentation.parent.components.pressableClick
import devs.grupo5.sportpro.presentation.parent.theme.ParentBorder
import devs.grupo5.sportpro.presentation.parent.theme.ParentGreenContainer
import devs.grupo5.sportpro.presentation.parent.theme.ParentNeonGreen
import devs.grupo5.sportpro.presentation.parent.theme.ParentStatusGold
import devs.grupo5.sportpro.presentation.parent.theme.ParentSurface
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextMuted
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextPrimary
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextSecondary

@Composable
fun ParentAnnouncementsScreen(
    announcements: List<ParentAnnouncement> = parentAnnouncements,
    onAnnouncementClick: (ParentAnnouncement) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Anuncios",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ParentTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Comunicados de la academia y el cuerpo técnico",
                fontSize = 13.sp,
                color = ParentTextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        items(announcements, key = { it.id }) { announcement ->
            AnnouncementCard(
                announcement = announcement,
                onClick = { onAnnouncementClick(announcement) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AnnouncementCard(
    announcement: ParentAnnouncement,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = if (announcement.isImportant) 1.5.dp else 1.dp,
                color = if (announcement.isImportant) ParentStatusGold.copy(alpha = 0.6f) else ParentBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .pressableClick { onClick() },
        colors = CardDefaults.cardColors(containerColor = ParentSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ParentGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (announcement.isImportant) Icons.Default.PushPin else Icons.Default.Campaign,
                        contentDescription = null,
                        tint = if (announcement.isImportant) ParentStatusGold else ParentNeonGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = announcement.author,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ParentTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = announcement.time,
                        fontSize = 11.sp,
                        color = ParentTextMuted
                    )
                }

                if (announcement.isImportant) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ParentStatusGold.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Importante",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ParentStatusGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = announcement.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ParentTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = announcement.body,
                fontSize = 13.sp,
                color = ParentTextSecondary,
                lineHeight = 19.sp
            )
        }
    }
}
