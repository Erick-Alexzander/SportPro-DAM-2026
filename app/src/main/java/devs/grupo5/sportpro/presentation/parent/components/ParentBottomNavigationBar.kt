package devs.grupo5.sportpro.presentation.parent.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
import devs.grupo5.sportpro.presentation.parent.theme.ParentBorder
import devs.grupo5.sportpro.presentation.parent.theme.ParentNeonGreen
import devs.grupo5.sportpro.presentation.parent.theme.ParentSurface
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextMuted

enum class ParentTab(val title: String, val icon: ImageVector, val route: String) {
    HOME("Inicio", Icons.Default.Home, "parent_home"),
    CHILD("Mi hijo", Icons.Default.FamilyRestroom, "parent_child"),
    CALENDAR("Calendario", Icons.Default.CalendarMonth, "parent_calendar"),
    ANNOUNCEMENTS("Anuncios", Icons.Default.Campaign, "parent_announcements"),
    PROFILE("Perfil", Icons.Default.Person, "parent_profile")
}

@Composable
fun ParentBottomNavigationBar(
    currentTab: ParentTab,
    onTabSelected: (ParentTab) -> Unit
) {
    NavigationBar(
        containerColor = ParentSurface,
        tonalElevation = 8.dp
    ) {
        ParentTab.entries.forEach { tab ->
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
                    selectedIconColor = ParentNeonGreen,
                    selectedTextColor = ParentNeonGreen,
                    indicatorColor = ParentBorder,
                    unselectedIconColor = ParentTextMuted,
                    unselectedTextColor = ParentTextMuted
                )
            )
        }
    }
}
