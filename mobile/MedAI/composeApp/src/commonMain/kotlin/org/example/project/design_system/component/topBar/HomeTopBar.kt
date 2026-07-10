package org.example.project.design_system.component.topBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.jetbrains.compose.resources.stringResource
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.home_greeting

import org.example.project.design_system.component.image.MedAIAsyncImage

@Composable
fun HomeTopBar(
    userName: String,
    avatarUrl: String?,
    onNotificationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MedAITheme.dimensions.extraLarge, vertical = MedAITheme.dimensions.large),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icons
        Row(horizontalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.small)) {
            IconButton(onClick = onNotificationClick) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = MedAITheme.colors.text.primary)
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MedAITheme.colors.text.primary)
            }
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = MedAITheme.colors.text.primary)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Greeting & User
        Column(horizontalAlignment = Alignment.End) {
            MedAIText(
                text = stringResource(Res.string.home_greeting),
                style = MedAITheme.textStyle.label.small,
                color = MedAITheme.colors.primary
            )
            MedAIText(
                text = userName,
                style = MedAITheme.textStyle.headline.small,
                color = MedAITheme.colors.text.primary
            )
        }

        Spacer(modifier = Modifier.width(MedAITheme.dimensions.medium))

        // Profile Pic
        MedAIAsyncImage(
            imageUrl = avatarUrl,
            nameForInitials = userName,
            modifier = Modifier
                .size(MedAITheme.dimensions.iconExtraLarge)
                .clip(CircleShape)
        )
    }
}
