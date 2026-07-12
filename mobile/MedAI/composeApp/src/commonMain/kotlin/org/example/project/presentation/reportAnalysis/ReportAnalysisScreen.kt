package org.example.project.presentation.reportAnalysis

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.example.project.core.presentation.image.rememberFilePicker
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.LocalDimensions
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.auth.UserRole
import org.example.project.domain.model.appointment.AppointmentDetail
import org.example.project.domain.model.appointment.AppointmentDetailStatus
import org.example.project.domain.model.report_analysis.ReportAnalysis
import org.example.project.domain.model.report_analysis.ReportAnalysisStatus

class ReportAnalysisScreen(private val patientUserId: String? = null) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<ReportAnalysisViewModel> {
            org.koin.core.parameter.parametersOf(patientUserId)
        }
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val dimensions = LocalDimensions.current
        val scope = rememberCoroutineScope()

        var isDropdownExpanded by remember { mutableStateOf(false) }

        // Navigation and effects
        LaunchedEffect(viewModel) {
            viewModel.effect.collectLatest { effect ->
                when (effect) {
                    is ReportAnalysisEffect.ShowSnackbar -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
            }
        }

        // Auto navigate to details when report changes and has result
        LaunchedEffect(state.selectedReport) {
            state.selectedReport?.let { report ->
                if (report.analysisStatus == ReportAnalysisStatus.COMPLETED && report.analysisResult != null) {
                    navigator.push(ReportAnalysisResultsScreen(report))
                    viewModel.onEvent(ReportAnalysisEvent.ViewReportDetails(null))
                }
            }
        }

        // File picker setup (real implementation on Android, neglected on iOS as requested)
        val targetPatientId = patientUserId ?: state.currentUserId ?: ""
        val filePicker = rememberFilePicker(
            allowedTypes = listOf("image/jpeg", "image/png", "image/webp", "application/pdf")
        ) { bytes, fileName ->
            viewModel.onEvent(
                ReportAnalysisEvent.UploadReport(
                    fileBytes = bytes,
                    fileName = fileName,
                    patientId = targetPatientId
                )
            )
        }

        MedAIScaffold(
            title = "Report AI Analyzer",
            onBackClick = { navigator.pop() },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                // Enabled for both SECRETARY and PATIENT on mobile as requested
                if (state.currentUserRole == UserRole.SECRETARY || state.currentUserRole == UserRole.PATIENT) {
                    ExtendedFloatingActionButton(
                        onClick = { filePicker.launch() },
                        icon = { Icon(Icons.Default.CloudUpload, contentDescription = null) },
                        text = { Text("Upload Report", style = MedAITheme.textStyle.label.medium) },
                        containerColor = MedAITheme.colors.primary,
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(dimensions.elevationMedium)
                    )
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MedAITheme.colors.background)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Title section
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "AI Sparkle",
                                        tint = MedAITheme.colors.accent,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    MedAIText(
                                        text = "AI Clinical Analysis",
                                        style = MedAITheme.textStyle.title.large.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                MedAIText(
                                    text = "Upload clinical report documents or diagnostic scan outputs. The AI will instantly analyze metrics, identify key findings, and explain them in plain language.",
                                    style = MedAITheme.textStyle.body.small,
                                    color = MedAITheme.colors.text.secondary
                                )
                            }
                        }
                    }

                    // Optional Appointment Linking Selection Dropdown
                    if (state.currentUserRole == UserRole.SECRETARY || state.currentUserRole == UserRole.PATIENT) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MedAITheme.colors.surface)
                                    .border(1.dp, MedAITheme.colors.text.tertiary.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                    .padding(16.dp)
                            ) {
                                MedAIText(
                                    text = "Link to Appointment (Optional)",
                                    style = MedAITheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Box {
                                    val selectedAppt = state.appointments.find { it.id == state.selectedAppointmentId }
                                    val dropdownText = if (selectedAppt != null) {
                                        "${selectedAppt.doctorName} - ${selectedAppt.specialty} (${selectedAppt.date.date})"
                                    } else {
                                        "Select appointment to link report..."
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MedAITheme.colors.background)
                                            .border(1.dp, MedAITheme.colors.text.tertiary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                            .clickable { isDropdownExpanded = true }
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = dropdownText,
                                            style = MedAITheme.textStyle.body.medium,
                                            color = if (selectedAppt != null) MedAITheme.colors.text.primary else MedAITheme.colors.text.secondary
                                        )
                                        Icon(
                                            imageVector = if (isDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            tint = MedAITheme.colors.text.secondary
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = isDropdownExpanded,
                                        onDismissRequest = { isDropdownExpanded = false },
                                        modifier = Modifier
                                            .fillMaxWidth(0.9f)
                                            .background(MedAITheme.colors.surface)
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("None (Do not link appointment)", style = MedAITheme.textStyle.body.medium) },
                                            onClick = {
                                                viewModel.onEvent(ReportAnalysisEvent.SelectAppointment(null))
                                                isDropdownExpanded = false
                                            }
                                        )
                                        state.appointments.forEach { appt ->
                                            val dateStr = appt.date.date.toString()
                                            val label = "${appt.doctorName} - ${appt.specialty} ($dateStr)"
                                            val isFinished = appt.status == AppointmentDetailStatus.FINISHED
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(label, style = MedAITheme.textStyle.body.medium)
                                                        if (isFinished) {
                                                            SuggestionChip(
                                                                onClick = {},
                                                                label = { Text("Finished", fontSize = 10.sp) },
                                                                colors = SuggestionChipDefaults.suggestionChipColors(
                                                                    containerColor = MedAITheme.colors.status.successContainer,
                                                                    labelColor = MedAITheme.colors.status.success
                                                                )
                                                            )
                                                        }
                                                    }
                                                },
                                                onClick = {
                                                    viewModel.onEvent(ReportAnalysisEvent.SelectAppointment(appt.id))
                                                    isDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Polling / Active Analysis Card
                    if (state.isPolling && state.pollingReport != null) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
                                border = BorderStroke(1.dp, MedAITheme.colors.accent.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                color = MedAITheme.colors.accent,
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            MedAIText(
                                                text = "Analyzing Report...",
                                                style = MedAITheme.textStyle.title.medium.copy(fontWeight = FontWeight.Bold),
                                                color = MedAITheme.colors.accent
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Simple custom Stepper based on pollingReport status
                                    val status = state.pollingReport?.analysisStatus
                                    StepperView(status = status)
                                }
                            }
                        }
                    }

                    // Upload Loader overlay
                    if (state.isUploading) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
                                border = BorderStroke(1.dp, MedAITheme.colors.primary.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MedAITheme.colors.primary
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        MedAIText("Uploading Document...", style = MedAITheme.textStyle.title.small)
                                        MedAIText("Encrypting and syncing to medical database", style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                                    }
                                }
                            }
                        }
                    }

                    // Reports List Header
                    item {
                        MedAIText(
                            text = "ANALYSIS HISTORY",
                            style = MedAITheme.textStyle.label.small.copy(fontWeight = FontWeight.Bold),
                            color = MedAITheme.colors.text.secondary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Error states
                    state.error?.let { err ->
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.status.errorContainer.copy(alpha = 0.2f)),
                                border = BorderStroke(1.dp, MedAITheme.colors.status.error),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Error, null, tint = MedAITheme.colors.status.error)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        MedAIText(
                                            text = err,
                                            style = MedAITheme.textStyle.body.medium,
                                            color = MedAITheme.colors.status.error
                                        )
                                    }
                                    IconButton(onClick = { viewModel.onEvent(ReportAnalysisEvent.DismissError) }) {
                                        Icon(Icons.Default.Close, null, tint = MedAITheme.colors.status.error)
                                    }
                                }
                            }
                        }
                    }

                    if (state.isLoading) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = MedAITheme.colors.primary)
                            }
                        }
                    } else if (state.reports.isEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FolderZip,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MedAITheme.colors.text.tertiary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    MedAIText(
                                        text = "No analysis history found",
                                        style = MedAITheme.textStyle.title.medium,
                                        color = MedAITheme.colors.text.secondary
                                    )
                                }
                            }
                        }
                    } else {
                        items(state.reports) { report ->
                            ReportAnalysisCard(
                                report = report,
                                isPollingThis = state.pollingReportId == report.id,
                                onClick = {
                                    if (report.analysisStatus == ReportAnalysisStatus.COMPLETED) {
                                        viewModel.onEvent(ReportAnalysisEvent.ViewReportDetails(report))
                                    } else if (report.analysisStatus == ReportAnalysisStatus.FAILED) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Analysis failed: ${report.analysisError ?: "Unknown error"}")
                                        }
                                    } else {
                                        viewModel.onEvent(ReportAnalysisEvent.TriggerAnalysis(report.id))
                                    }
                                }
                            )
                        }
                    }

                    // Extra space at bottom
                    item {
                        Spacer(modifier = Modifier.height(88.dp))
                    }
                }
            }
        }
    }

    @Composable
    private fun StepperView(status: ReportAnalysisStatus?) {
        val step = when (status) {
            ReportAnalysisStatus.QUEUED -> 1
            ReportAnalysisStatus.PROCESSING -> 2
            ReportAnalysisStatus.COMPLETED -> 3
            ReportAnalysisStatus.FAILED -> 3
            else -> 0
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepItem(number = 1, title = "Queued", active = step >= 1, completed = step > 1)
            StepDivider(active = step > 1)
            StepItem(number = 2, title = "Analyzing", active = step >= 2, completed = step > 2)
            StepDivider(active = step > 2)
            StepItem(
                number = 3,
                title = if (status == ReportAnalysisStatus.FAILED) "Failed" else "Complete",
                active = step >= 3,
                completed = step >= 3,
                isError = status == ReportAnalysisStatus.FAILED
            )
        }
    }

    @Composable
    private fun RowScope.StepItem(number: Int, title: String, active: Boolean, completed: Boolean, isError: Boolean = false) {
        val color = when {
            isError -> MedAITheme.colors.status.error
            completed -> MedAITheme.colors.status.success
            active -> MedAITheme.colors.accent
            else -> MedAITheme.colors.text.tertiary
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(alpha = 0.15f))
                    .border(2.dp, color, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (completed && !isError) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp), tint = color)
                } else if (isError) {
                    Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp), tint = color)
                } else {
                    Text(number.toString(), color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, color = color, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }

    @Composable
    private fun RowScope.StepDivider(active: Boolean) {
        Box(
            modifier = Modifier
                .height(2.dp)
                .weight(0.5f)
                .background(if (active) MedAITheme.colors.status.success else MedAITheme.colors.text.tertiary.copy(alpha = 0.3f))
        )
    }

    @Composable
    private fun ReportAnalysisCard(report: ReportAnalysis, isPollingThis: Boolean, onClick: () -> Unit) {
        val statusColor = when (report.analysisStatus) {
            ReportAnalysisStatus.QUEUED -> MedAITheme.colors.status.warning
            ReportAnalysisStatus.PROCESSING -> MedAITheme.colors.accent
            ReportAnalysisStatus.COMPLETED -> MedAITheme.colors.status.success
            ReportAnalysisStatus.FAILED -> MedAITheme.colors.status.error
            else -> MedAITheme.colors.text.secondary
        }

        val statusText = when (report.analysisStatus) {
            ReportAnalysisStatus.QUEUED -> "Queued"
            ReportAnalysisStatus.PROCESSING -> "Analyzing"
            ReportAnalysisStatus.COMPLETED -> "Ready"
            ReportAnalysisStatus.FAILED -> "Analysis Failed"
            else -> "Awaiting analysis"
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (report.path.endsWith(".pdf", ignoreCase = true)) Icons.Default.PictureAsPdf else Icons.Default.Description,
                        contentDescription = null,
                        tint = MedAITheme.colors.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        val fileName = report.path.split("/").lastOrNull() ?: "report.pdf"
                        MedAIText(
                            text = fileName,
                            style = MedAITheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.15f)),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = statusText,
                                    color = statusColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            MedAIText(
                                text = report.createdAt.take(10),
                                style = MedAITheme.textStyle.body.small,
                                color = MedAITheme.colors.text.secondary
                            )
                        }
                    }
                }

                if (isPollingThis) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MedAITheme.colors.accent,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = if (report.analysisStatus == ReportAnalysisStatus.COMPLETED) Icons.Default.ChevronRight else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = MedAITheme.colors.text.secondary
                    )
                }
            }
        }
    }
}
