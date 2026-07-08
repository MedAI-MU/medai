package org.example.project.presentation.doctorsScreen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.info_button
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.LocalDimensions
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.doctor.Doctor
import org.jetbrains.compose.resources.stringResource

@Composable
fun DoctorCard(
    doctor: Doctor,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = dimensions.extraSmall, shape = RoundedCornerShape(dimensions.large))
            .clip(RoundedCornerShape(dimensions.large))
            .background(MedAITheme.colors.surface)
            .clickable { onClick() }
            .padding(dimensions.large)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 1. Doctor Image
            Box(
                modifier = Modifier
                    .size(dimensions.spacing64 + dimensions.large)
                    .clip(CircleShape)
                    .background(MedAITheme.colors.neutral) // Placeholder for AsyncImage
            ) {
                // In production, we will use Coil/Kamel here:
                // AsyncImage(data = doctor.imageUrl, ...)
            }

            Spacer(modifier = Modifier.width(dimensions.large))

            // 2. Info Column
            Column(modifier = Modifier.weight(1f)) {
                MedAIText(
                    text = doctor.name,
                    style = MedAITheme.textStyle.title.medium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MedAITheme.colors.primary
                    )
                )

                MedAIText(
                    text = doctor.specialty,
                    style = MedAITheme.textStyle.label.medium,
                    color = MedAITheme.colors.text.secondary
                )

                Spacer(modifier = Modifier.height(dimensions.medium))

                // 3. Actions Row (Info Button + Icons)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Info Button (Outlined pill)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(dimensions.radiusRound))
                            .border(1.dp, color = MedAITheme.colors.primary, RoundedCornerShape(dimensions.radiusRound))
                            .padding(horizontal = dimensions.extraLarge, vertical = dimensions.small)
                            .clickable { /* Info Action */ }
                    ) {
                        MedAIText(
                            text = stringResource(Res.string.info_button),
                            style = MedAITheme.textStyle.label.small.copy(fontWeight = FontWeight.Bold),
                            color = MedAITheme.colors.primary
                        )
                    }

                    // Icons
                    Row(horizontalArrangement = Arrangement.spacedBy(dimensions.small)) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Schedule",
                            tint = MedAITheme.colors.primary.copy(alpha = 0.6f),
                            modifier = Modifier.size(dimensions.extraLarge)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = "Help",
                            tint = MedAITheme.colors.primary.copy(alpha = 0.6f),
                            modifier = Modifier.size(dimensions.extraLarge)
                        )
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorite",
                            tint = MedAITheme.colors.primary.copy(alpha = 0.6f),
                            modifier = Modifier.size(dimensions.extraLarge)
                        )
                    }
                }
            }
        }
    }
}
