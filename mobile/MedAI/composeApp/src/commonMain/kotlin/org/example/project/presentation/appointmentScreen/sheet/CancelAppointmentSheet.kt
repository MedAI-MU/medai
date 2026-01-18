package org.example.project.presentation.appointmentScreen.sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.RadioButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.design_system.component.button.ButtonVariant
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.button.radioButton.MedAIRadioButton
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.component.textFields.MedAiTextArea
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.CancelReason

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancelAppointmentSheet(
    onDismiss: () -> Unit,
    onConfirm: (reasonId: String, otherReason: String) -> Unit,
    reasons: List<CancelReason>
) {
    var selectedReasonId by remember { mutableStateOf(reasons.firstOrNull()?.id ?: "") }
    var otherReasonText by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MedAITheme.colors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 24.dp)
        ) {
            MedAIText("Cancel Appointment", style = MedAITheme.textStyle.headline.small.copy(fontWeight = FontWeight.Bold), color = MedAITheme.colors.status.error)
            Spacer(modifier = Modifier.height(8.dp))
            MedAIText("Please select the reason for cancellations:", style = MedAITheme.textStyle.body.medium, color = MedAITheme.colors.text.secondary)

            Spacer(modifier = Modifier.height(16.dp))

            // Radio Buttons
            reasons.forEach { reason ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .selectable(
                            selected = (reason.id == selectedReasonId),
                            onClick = { selectedReasonId = reason.id },
                            role = RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MedAIRadioButton(
                        selected = (reason.id == selectedReasonId),
                        onClick = { selectedReasonId = reason.id }
                    )
                    Spacer(Modifier.width(8.dp))
                    MedAIText(
                        text = reason.reason,
                        style = MedAITheme.textStyle.body.large
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            MedAIText("Others (Optional)", style = MedAITheme.textStyle.label.medium)
            Spacer(modifier = Modifier.height(8.dp))
            MedAiTextArea(
                value = otherReasonText,
                onValueChange = { otherReasonText = it },
                placeholder = "Add your reason...",
                modifier = Modifier.height(100.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            MedAIButton(
                text = "Confirm Cancellation",
                onClick = { onConfirm(selectedReasonId, otherReasonText) },
                variant = ButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
