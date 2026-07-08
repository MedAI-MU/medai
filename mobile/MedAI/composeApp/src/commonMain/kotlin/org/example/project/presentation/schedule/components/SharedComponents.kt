package org.example.project.presentation.schedule

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.example.project.design_system.theme.MedAITheme
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.appBar.MedAiAppBar
import org.example.project.domain.model.schedule.*
import org.koin.core.parameter.parametersOf


// ==================== Shared Components ====================

@Composable
fun EmptyContent(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    message: String,
    subtitle: String
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                icon,
                contentDescription = null,
                tint = MedAITheme.colors.neutral.copy(alpha = 0.4f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MedAITheme.textStyle.title.medium,
                fontWeight = FontWeight.Bold,
                color = MedAITheme.colors.text.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MedAITheme.textStyle.body.small,
                color = MedAITheme.colors.text.tertiary
            )
        }
    }
}

@Composable
fun ErrorContent(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = MedAITheme.colors.status.error,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = MedAITheme.textStyle.body.medium,
                color = MedAITheme.colors.status.error
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = MedAITheme.colors.primary)
            ) {
                Text("Retry")
            }
        }
    }
}
