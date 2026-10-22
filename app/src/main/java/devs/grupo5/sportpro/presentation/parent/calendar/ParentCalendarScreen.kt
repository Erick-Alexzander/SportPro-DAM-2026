package devs.grupo5.sportpro.presentation.parent.calendar

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.presentation.parent.components.pressableClick
import devs.grupo5.sportpro.presentation.parent.theme.ParentBorder
import devs.grupo5.sportpro.presentation.parent.theme.ParentGreenContainer
import devs.grupo5.sportpro.presentation.parent.theme.ParentNeonGreen
import devs.grupo5.sportpro.presentation.parent.theme.ParentSurface
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextMuted
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextPrimary
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextSecondary
import devs.grupo5.sportpro.presentation.parent.theme.ParentWarning

@Composable
fun ParentCalendarScreen(
    childCategory: String = "",
    onEventClick: (ParentEvent) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(parentEventFilters.first()) }

    val filteredEvents = when (selectedFilter) {
        "Entrenamientos" -> parentEvents.filter { it.type == ParentEventType.ENTRENAMIENTO }
        "Partidos" -> parentEvents.filter { it.type == ParentEventType.PARTIDO }
        else -> parentEvents
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Calendario",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = ParentTextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (childCategory.isNotBlank()) {
                "Agenda de $childCategory · solo lectura, la academia gestiona las sesiones"
            } else {
                "Agenda de la academia · solo lectura"
            },
            fontSize = 13.sp,
            color = ParentTextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(parentEventFilters) { filter ->
                CalendarFilterChip(
                    label = filter,
                    isSelected = filter == selectedFilter,
                    onClick = { selectedFilter = filter }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredEvents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay eventos para este filtro.",
                    color = ParentTextMuted,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredEvents, key = { it.id }) { event ->
                    ParentEventCard(
                        event = event,
                        onClick = { onEventClick(event) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun CalendarFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) ParentNeonGreen else ParentSurface)
            .border(
                1.dp,
                if (isSelected) ParentNeonGreen else ParentBorder,
                RoundedCornerShape(20.dp)
            )
            .pressableClick { onClick() }
            .padding(horizontal = 16.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color(0xFF00210B) else ParentTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ParentEventCard(
    event: ParentEvent,
    onClick: () -> Unit
) {
    val isMatch = event.type == ParentEventType.PARTIDO

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, ParentBorder, RoundedCornerShape(16.dp))
            .pressableClick { onClick() },
        colors = CardDefaults.cardColors(containerColor = ParentSurface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 58.dp, height = 64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isMatch) ParentWarning else ParentNeonGreen),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = event.day,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF00210B),
                        lineHeight = 22.sp
                    )
                    Text(
                        text = event.month,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00210B)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ParentGreenContainer)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = event.type.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ParentNeonGreen
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = event.time,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ParentTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = event.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParentTextPrimary,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isMatch) Icons.Default.SportsSoccer else Icons.AutoMirrored.Filled.DirectionsRun,
                        contentDescription = null,
                        tint = ParentTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.subtitle,
                        fontSize = 12.sp,
                        color = ParentTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = ParentTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.place,
                        fontSize = 12.sp,
                        color = ParentTextMuted
                    )
                }
            }
        }
    }
}
