package org.example.project.design_system.component.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import coil3.compose.SubcomposeAsyncImage
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme

@Composable
fun MedAIAsyncImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    nameForInitials: String? = null,
    contentScale: ContentScale = ContentScale.Crop
) {
    val initials = nameForInitials?.trim()?.split(" ")
        ?.filter { it.isNotEmpty() }
        ?.take(2)
        ?.map { it.first().uppercaseChar() }
        ?.joinToString("") ?: ""

    val placeholderGradient = Brush.linearGradient(
        colors = listOf(
            MedAITheme.colors.primary,
            MedAITheme.colors.primary.copy(alpha = 0.6f)
        )
    )

    if (!imageUrl.isNullOrBlank()) {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize().background(MedAITheme.colors.surface),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.fillMaxSize(0.4f),
                        color = MedAITheme.colors.primary,
                        strokeWidth = 2.dp
                    )
                }
            },
            error = {
                PlaceholderView(
                    initials = initials,
                    gradient = placeholderGradient,
                    modifier = Modifier.fillMaxSize()
                )
            }
        )
    } else {
        PlaceholderView(
            initials = initials,
            gradient = placeholderGradient,
            modifier = modifier
        )
    }
}

@Composable
private fun PlaceholderView(
    initials: String,
    gradient: Brush,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        if (initials.isNotEmpty()) {
            MedAIText(
                text = initials,
                style = MedAITheme.textStyle.headline.small.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                color = Color.White
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.fillMaxSize(0.5f)
            )
        }
    }
}
