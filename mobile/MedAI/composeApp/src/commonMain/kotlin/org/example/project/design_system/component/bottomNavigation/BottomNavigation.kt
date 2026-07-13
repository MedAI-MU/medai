package org.example.project.design_system.component.bottomNavigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.example.project.design_system.theme.MedAITheme
import org.jetbrains.compose.ui.tooling.preview.Preview

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun MedAIBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    items: List<BottomNavItem>
) {
    // Define your navigation items
    val isDark = isSystemInDarkTheme()
    val barColor = if (isDark) MedAITheme.colors.background else MedAITheme.colors.surface

    val elevation = if (isDark) 0.dp else 8.dp
    val barHeight = 64.dp
    Surface(
        modifier = modifier,
        color = barColor,
        shadowElevation = elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(barHeight),
            horizontalArrangement = Arrangement.SpaceAround, // Distribute items evenly
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                val iconColor = if (isSelected) MedAITheme.colors.primary else MedAITheme.colors.text.secondary.copy(alpha = 0.5f)

                // 2. Custom Item Container
                Box(
                    modifier = Modifier
                        .weight(1f) // Make each item take equal width
                        .fillMaxHeight()
                        .clip(CircleShape) // Ripple shape
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onNavigate(item.route) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.name,
                        tint = iconColor,
                        // 3. Control Icon Size directly here if needed
                        // modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun MedAIBottomNavigationPreview(){
    MedAITheme {
        var currentRoute by remember { mutableStateOf("home") }
        Scaffold(
            bottomBar = {
                MedAIBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigate = { newRoute -> currentRoute = newRoute },
                    items = listOf(
                        BottomNavItem("Home", "home", Icons.Default.Home),
                        BottomNavItem("Calendar", "calendar", Icons.Default.CalendarMonth),
                        //BottomNavItem("Chat", "chat", Icons.Default.ChatBubbleOutline)
                    ),
                    modifier = Modifier
                )
            },
            containerColor = MedAITheme.colors.background
        ){

        }
    }
}
