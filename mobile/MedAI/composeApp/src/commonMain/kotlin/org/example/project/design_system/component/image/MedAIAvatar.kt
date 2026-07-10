package org.example.project.design_system.component.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme

/**
 * A reusable avatar component that attempts to load the actual user avatar
 * from [url] first, and gracefully falls back to a styled "First Letter"
 * initials circle if the URL is null, empty, or fails to load.
 *
 * The fallback uses the existing brand-tinted design: a light-primary background
 * circle with bold primary-colored initials derived from [name].
 */
@Composable
fun MedAIAvatar(
    url: String?,
    name: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val initials = name.split(" ")
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.take(1).uppercase() }

    if (!url.isNullOrBlank()) {
        SubcomposeAsyncImage(
            model = url,
            contentDescription = name,
            modifier = modifier.clip(CircleShape),
            contentScale = contentScale,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MedAITheme.colors.primary.copy(alpha = 0.1f)),
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
                InitialsFallback(
                    initials = initials,
                    modifier = Modifier.fillMaxSize()
                )
            }
        )
    } else {
        InitialsFallback(
            initials = initials,
            modifier = modifier
        )
    }
}

/**
 * The existing "First Letter" fallback design:
 * A circle with a light-primary tint background and bold primary-colored initials.
 */
@Composable
private fun InitialsFallback(
    initials: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MedAITheme.colors.primary.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        MedAIText(
            text = initials.ifBlank { "?" },
            style = MedAITheme.textStyle.title.medium.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            color = MedAITheme.colors.primary
        )
    }
}
