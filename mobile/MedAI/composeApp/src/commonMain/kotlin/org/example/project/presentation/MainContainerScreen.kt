package org.example.project.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import org.example.project.presentation.schedule.ScheduleScreen
import org.example.project.presentation.secretary.doctors.SecretaryDoctorListScreen
import org.example.project.presentation.chatScreen.ChatListScreen
import org.example.project.presentation.homeScreen.HomeScreen
import org.example.project.presentation.profileScreen.ProfileScreen

import org.example.project.domain.repository.auth.UserSessionManager
import org.example.project.domain.model.auth.UserRole
import org.example.project.presentation.doctor.dashboard.DoctorDashboardScreen
import org.example.project.presentation.secretary.dashboard.SecretaryDashboardScreen
import org.koin.compose.koinInject

class MainContainerScreen : Screen {
    @Composable
    override fun Content() {
        TabNavigator(HomeTab) {

            val tabNavigator = LocalTabNavigator.current

            // Map the Voyager Tabs to our Navigation Item data class
            val userSessionManager = koinInject<UserSessionManager>()
            val role = androidx.compose.runtime.produceState<UserRole?>(initialValue = null) {
                value = userSessionManager.getUserRole()
            }.value

            // Map the Voyager Tabs to our Navigation Item data class
            val navItems = mutableListOf<BottomNavItem>()

            navItems.add(BottomNavItem("Home", "home", Icons.Default.Home))
            navItems.add(BottomNavItem("Messages", "messages", Icons.Default.ChatBubbleOutline))

            if (role == UserRole.DOCTOR || role == UserRole.SECRETARY) {
                 navItems.add(BottomNavItem("Schedule", "schedule", Icons.Default.CalendarMonth))
            }

            navItems.add(BottomNavItem("Profile", "profile", Icons.Default.Person))

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
                                "schedule" -> if(role == UserRole.DOCTOR || role == UserRole.SECRETARY) tabNavigator.current = ScheduleTab
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
        val userSessionManager = koinInject<UserSessionManager>()
        val role = androidx.compose.runtime.produceState<UserRole?>(initialValue = null) {
            value = userSessionManager.getUserRole()
        }.value

        when (role) {
            null -> {
                // Still loading role — show loading indicator instead of flashing patient screen
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    androidx.compose.material3.CircularProgressIndicator()
                }
            }
            UserRole.DOCTOR -> DoctorDashboardScreen().Content()
            UserRole.SECRETARY -> SecretaryDashboardScreen().Content()
            else -> HomeScreen().Content()
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
        val userSessionManager = koinInject<UserSessionManager>()
        val role = androidx.compose.runtime.produceState<UserRole?>(initialValue = null) {
            value = userSessionManager.getUserRole()
        }.value

        when (role) {
            null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    androidx.compose.material3.CircularProgressIndicator()
                }
            }
            UserRole.DOCTOR -> org.example.project.presentation.doctor.chat.DoctorChatListScreen().Content()
            else -> ChatListScreen().Content()
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
        val userSessionManager = koinInject<UserSessionManager>()
        val role = androidx.compose.runtime.produceState<UserRole?>(initialValue = null) {
            value = userSessionManager.getUserRole()
        }.value
        val userId = androidx.compose.runtime.produceState<String?>(initialValue = null) {
            value = userSessionManager.getUserId()
        }.value

        if (userId == null || role == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                androidx.compose.material3.CircularProgressIndicator()
            }
            return
        }

        if (role == UserRole.SECRETARY) {
            SecretaryDoctorListScreen().Content()
        } else {
            val doctorId = userId.toIntOrNull() ?: 0
            ScheduleScreen(doctorId = doctorId).Content()
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
        ProfileScreen().Content()
    }
}
