package org.example.project.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.example.project.design_system.component.bottomNavigation.BottomNavItem
import org.example.project.design_system.component.bottomNavigation.MedAIBottomNavigation
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.theme.MedAITheme

class MainContainerScreen : Screen {
    @Composable
    override fun Content() {
        TabNavigator(HomeTab) {

            val tabNavigator = LocalTabNavigator.current

            // Map the Voyager Tabs to our Navigation Item data class
            val navItems = listOf(
                BottomNavItem("Home", "home", Icons.Default.Home),
                BottomNavItem("Messages", "messages", Icons.Default.ChatBubbleOutline),
                BottomNavItem("Schedule", "schedule", Icons.Default.CalendarMonth),
                BottomNavItem("Profile", "profile", Icons.Default.Person)
            )

            MedAIScaffold (
                containerColor = MedAITheme.colors.background,
                content = { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        CurrentTab()
                    }
                },
                bottomBar = {
                    MedAIBottomNavigation(
                        currentRoute = getRouteFromTab(tabNavigator.current),
                        onNavigate = { route ->
                            when(route) {
                                "home" -> tabNavigator.current = HomeTab
                                "messages" -> tabNavigator.current = MessagesTab
                                "schedule" -> tabNavigator.current = ScheduleTab
                                "profile" -> tabNavigator.current = ProfileTab
                            }
                        },
                        items = navItems
                    )
                }
            )
        }
    }

    private fun getRouteFromTab(tab: Tab): String {
        return when (tab) {
            HomeTab -> "home"
            MessagesTab -> "messages"
            ScheduleTab -> "schedule"
            ProfileTab -> "profile"
            else -> ""
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    NavigationBarItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = { tab.options.icon?.let { Icon(painter = it, contentDescription = tab.options.title) } },
        label = { Text(tab.options.title) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MedAITheme.colors.primary,
            indicatorColor = Color.Transparent
        )
    )
}

// --- TABS ---

object HomeTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Home)
            return remember { TabOptions(index = 0u, title = "Home", icon = icon) }
        }

    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Home Screen (Image 5)")
            // You can add buttons here to test navigation to inner screens
            // val navigator = LocalNavigator.currentOrThrow.parent // Get parent navigator to leave tabs
        }
    }
}

object MessagesTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.ChatBubbleOutline)
            return remember { TabOptions(index = 1u, title = "Messages", icon = icon) }
        }

    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Messages/Notifications (Image 9)")
        }
    }
}

object ScheduleTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.CalendarMonth)
            return remember { TabOptions(index = 2u, title = "Schedule", icon = icon) }
        }

    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Schedule (Image 7)")
        }
    }
}

object ProfileTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Person)
            return remember { TabOptions(index = 3u, title = "Profile", icon = icon) }
        }

    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Profile (Image 8)")
        }
    }
}
