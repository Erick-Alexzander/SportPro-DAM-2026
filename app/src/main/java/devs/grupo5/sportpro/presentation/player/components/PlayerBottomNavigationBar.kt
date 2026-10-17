package devs.grupo5.sportpro.presentation.player.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.presentation.player.theme.PlayerBorder
import devs.grupo5.sportpro.presentation.player.theme.PlayerNeonGreen
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurface
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextMuted

enum class PlayerTab(val title: String, val icon: ImageVector) {
    HOME("Inicio", Icons.Default.Home),
    CALENDAR("Calendario", Icons.Default.CalendarMonth),
    MATCHES("Partidos", Icons.Default.SportsSoccer),
    COMMUNITY("Comunidad", Icons.Default.Forum),
    PROFILE("Perfil", Icons.Default.Person)
}

@Composable
fun PlayerBottomNavigationBar(
    currentTab: PlayerTab,
    onTabSelected: (PlayerTab) -> Unit
) {
    NavigationBar(
        containerColor = PlayerSurface,
        tonalElevation = 8.dp
    ) {
        PlayerTab.entries.forEach { tab ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PlayerNeonGreen,
                    selectedTextColor = PlayerNeonGreen,
                    indicatorColor = PlayerBorder,
                    unselectedIconColor = PlayerTextMuted,
                    unselectedTextColor = PlayerTextMuted
                )
            )
        }
    }
}
