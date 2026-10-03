package devs.grupo5.sportpro.presentation.trainer.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
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
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBorder
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreen
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextMuted

enum class TrainerTab(val title: String, val icon: ImageVector, val route: String) {
    DASHBOARD("Inicio", Icons.Default.Home, "trainer_dashboard"),
    TRAINING("Entrenam.", Icons.Default.FitnessCenter, "trainer_training"),
    MATCHES("Partidos", Icons.Default.SportsSoccer, "trainer_matches"),
    TEAMS("Equipo", Icons.Default.Groups, "trainer_teams"),
    COMMUNITY("Comunidad", Icons.Default.Forum, "trainer_community")
}

@Composable
fun TrainerBottomNavigationBar(
    currentTab: TrainerTab,
    onTabSelected: (TrainerTab) -> Unit
) {
    NavigationBar(
        containerColor = SportProCardBackground,
        tonalElevation = 8.dp
    ) {
        TrainerTab.entries.forEach { tab ->
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
                    selectedIconColor = SportProGreen,
                    selectedTextColor = SportProGreen,
                    indicatorColor = SportProCardBorder,
                    unselectedIconColor = SportProTextMuted,
                    unselectedTextColor = SportProTextMuted
                )
            )
        }
    }
}
