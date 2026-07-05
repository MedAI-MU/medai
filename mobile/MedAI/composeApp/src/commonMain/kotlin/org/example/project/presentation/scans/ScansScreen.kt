package org.example.project.presentation.scans

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.Clock
import org.example.project.core.presentation.image.rememberImagePicker
import org.example.project.core.presentation.image.toImageBitmap
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.LocalDimensions
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.auth.UserRole
import org.example.project.domain.model.scans.Scan
import org.example.project.domain.model.scans.Report
import org.example.project.domain.usecase.scans.GetScanImageFileUseCase

class ScansScreen(private val patientUserId: String? = null) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<ScansViewModel> {
            org.koin.core.parameter.parametersOf(patientUserId)
        }
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val dimensions = LocalDimensions.current

        // Handling snackbar notifications
        LaunchedEffect(viewModel) {
            viewModel.effect.collectLatest { effect ->
                when (effect) {
                    is ScansEffect.ShowSnackbar -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                    ScansEffect.NavigateBack -> {
                        navigator.pop()
                    }
                }
            }
        }

        // Upload Pickers
        val targetPatientId = patientUserId ?: state.currentUserId ?: ""
        var activeScanIdForReportUpload by remember { mutableStateOf<String?>(null) }

        val scanPicker = rememberImagePicker { bytes ->
            viewModel.onEvent(ScansEvent.UploadScan(targetPatientId, null, listOf(bytes)))
        }

        val reportPicker = rememberImagePicker { bytes ->
            activeScanIdForReportUpload?.let { scanId ->
                viewModel.onEvent(ScansEvent.UploadReport(targetPatientId, scanId, bytes, "report_${Clock.System.now().toEpochMilliseconds()}.png"))
            }
        }

        MedAIScaffold(
            title = "Scans & Reports",
            onBackClick = { navigator.pop() },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                // Secretaries can upload new scans
                if (state.currentUserRole == UserRole.SECRETARY) {
                    ExtendedFloatingActionButton(
                        onClick = { scanPicker.launch() },
                        icon = { Icon(Icons.Default.CloudUpload, contentDescription = null) },
                        text = { Text("Upload Scan", style = MedAITheme.textStyle.label.medium) },
                        containerColor = MedAITheme.colors.primary,
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(dimensions.elevationMedium)
                    )
                }
            }
        ) {
            Box(modifier = Modifier.fillMaxSize().background(MedAITheme.colors.background)) {
                when {
                    state.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MedAITheme.colors.primary)
                        }
                    }
                    state.error != null -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.BrokenImage, null, modifier = Modifier.size(64.dp), tint = MedAITheme.colors.status.error)
                            Spacer(modifier = Modifier.height(16.dp))
                            MedAIText(state.error ?: "Error occurred", style = MedAITheme.textStyle.title.medium, color = MedAITheme.colors.status.error, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(16.dp))
                            MedAIButton(text = "Retry", onClick = { viewModel.onEvent(ScansEvent.LoadScans) })
                        }
                    }
                    state.scans.isEmpty() -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(32.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(36.dp))
                                    .background(MedAITheme.colors.primary.copy(alpha = 0.1f))
                                    .padding(16.dp),
                                tint = MedAITheme.colors.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            MedAIText("No Scans Or Reports Found", style = MedAITheme.textStyle.title.medium, color = MedAITheme.colors.text.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            MedAIText(
                                text = if (state.currentUserRole == UserRole.SECRETARY) {
                                    "Tap the button below to upload the patient's first medical scan plate."
                                } else {
                                    "There are currently no uploaded scan plates or analytical reports for your profile."
                                },
                                style = MedAITheme.textStyle.body.medium,
                                color = MedAITheme.colors.text.secondary,
                                textAlign = TextAlign.Center
                            )
                            if (state.currentUserRole == UserRole.SECRETARY) {
                                Spacer(modifier = Modifier.height(24.dp))
                                MedAIButton(text = "Upload First Scan", onClick = { scanPicker.launch() })
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.scans) { scan ->
                                ScanCard(
                                    scan = scan,
                                    isExpanded = state.expandedScanIds.contains(scan.id),
                                    reports = state.reports[scan.id] ?: emptyList(),
                                    reportsLoading = state.reportsLoading[scan.id] ?: false,
                                    currentUserRole = state.currentUserRole,
                                    onToggleExpand = {
                                        viewModel.onEvent(ScansEvent.ToggleScanExpanded(scan.id, targetPatientId))
                                    },
                                    onDeleteScan = {
                                        viewModel.onEvent(ScansEvent.DeleteScan(targetPatientId, scan.id))
                                    },
                                    onUploadReportClick = {
                                        activeScanIdForReportUpload = scan.id
                                        reportPicker.launch()
                                    },
                                    onDeleteReport = { reportId ->
                                        viewModel.onEvent(ScansEvent.DeleteReport(targetPatientId, reportId))
                                    },
                                    onImageClick = { imageId, path ->
                                        viewModel.onEvent(ScansEvent.FetchScanImage(targetPatientId, imageId, path.split("/").last()))
                                    },
                                    onReportClick = { report ->
                                        viewModel.onEvent(ScansEvent.FetchReportFile(targetPatientId, report.id, report.path.split("/").last()))
                                    },
                                    getScanImageFileUseCase = org.koin.compose.koinInject()
                                )
                            }
                            // Extra space to prevent fab overlap
                            item {
                                Spacer(modifier = Modifier.height(88.dp))
                            }
                        }
                    }
                }

                // File Downloader Progress overlay
                if (state.isDownloadingFile || state.isUploadingScan || state.isUploadingReport) {
                    val message = when {
                        state.isDownloadingFile -> "Downloading file from secure vault..."
                        state.isUploadingScan -> "Uploading medical scan plate..."
                        else -> "Uploading medical report document..."
                    }
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.width(280.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = MedAITheme.colors.primary)
                                Spacer(modifier = Modifier.height(16.dp))
                                MedAIText(message, style = MedAITheme.textStyle.label.medium, color = MedAITheme.colors.text.primary, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }

                // Lightbox File Viewer Dialog
                state.viewingFileBytes?.let { bytes ->
                    Dialog(
                        onDismissRequest = { viewModel.onEvent(ScansEvent.CloseFileViewer) },
                        properties = DialogProperties(usePlatformDefaultWidth = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black)
                                .safeDrawingPadding()
                        ) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                // Top header bar in fullscreen dialog
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        MedAIText(
                                            text = state.viewingFileName ?: "File Preview",
                                            style = MedAITheme.textStyle.title.medium,
                                            color = Color.White
                                        )
                                        MedAIText(
                                            text = if (state.viewingFileType == FileType.IMAGE) "Secure Diagnostic Image" else "Secure Document",
                                            style = MedAITheme.textStyle.body.small,
                                            color = Color.Gray
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.onEvent(ScansEvent.CloseFileViewer) },
                                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(alpha = 0.1f))
                                    ) {
                                        Icon(Icons.Default.Close, null, tint = Color.White)
                                    }
                                }

                                // Main Preview Content
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (state.viewingFileType == FileType.IMAGE) {
                                        val bitmap = remember(bytes) { bytes.toImageBitmap() }
                                        Image(
                                            bitmap = bitmap,
                                            contentDescription = "Diagnostic Scan File",
                                            modifier = Modifier.fillMaxSize().padding(8.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    } else {
                                        // PDF Vault view
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Description,
                                                contentDescription = null,
                                                tint = MedAITheme.colors.primary,
                                                modifier = Modifier.size(80.dp)
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            MedAIText(
                                                text = "PDF Vault Decrypted",
                                                style = MedAITheme.textStyle.title.large,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            MedAIText(
                                                text = "File successfully retrieved and verified. Size: ${(bytes.size / 1024.0).toInt()} KB",
                                                style = MedAITheme.textStyle.body.medium,
                                                color = Color.LightGray,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(24.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                                MedAIButton(
                                                    text = "Done",
                                                    onClick = { viewModel.onEvent(ScansEvent.CloseFileViewer) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ScanCard(
        scan: Scan,
        isExpanded: Boolean,
        reports: List<Report>,
        reportsLoading: Boolean,
        currentUserRole: UserRole?,
        onToggleExpand: () -> Unit,
        onDeleteScan: () -> Unit,
        onUploadReportClick: () -> Unit,
        onDeleteReport: (String) -> Unit,
        onImageClick: (String, String) -> Unit,
        onReportClick: (Report) -> Unit,
        getScanImageFileUseCase: GetScanImageFileUseCase
    ) {
        val dimensions = LocalDimensions.current
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
            elevation = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface).let {
                CardDefaults.cardElevation(defaultElevation = 2.dp)
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Scan Card Header
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onToggleExpand() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = MedAITheme.colors.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            MedAIText(
                                text = "Scan ID: #${scan.id}",
                                style = MedAITheme.textStyle.title.medium.copy(fontWeight = FontWeight.Bold)
                            )
                            MedAIText(
                                text = "Date: ${scan.createdAt.take(10)}",
                                style = MedAITheme.textStyle.body.small,
                                color = MedAITheme.colors.text.secondary
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (currentUserRole == UserRole.SECRETARY) {
                            IconButton(onClick = onDeleteScan) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Scan", tint = MedAITheme.colors.status.error)
                            }
                        }
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MedAITheme.colors.text.secondary
                        )
                    }
                }

                // Collapsible Content
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MedAITheme.colors.text.tertiary.copy(alpha = 0.2f)
                        )

                        // Images Diagnostic Box
                        MedAIText(
                            text = "DIAGNOSTIC PLATES (${scan.images.size})",
                            style = MedAITheme.textStyle.label.small.copy(fontWeight = FontWeight.Bold),
                            color = MedAITheme.colors.text.secondary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (scan.images.isEmpty()) {
                            MedAIText("No diagnostic plates attached.", style = MedAITheme.textStyle.body.medium)
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                scan.images.forEach { image ->
                                    ScanImageThumbnail(
                                        patientUserId = scan.patientUserId,
                                        imageId = image.id,
                                        getScanImageFileUseCase = getScanImageFileUseCase,
                                        modifier = Modifier
                                            .size(90.dp)
                                            .border(1.dp, MedAITheme.colors.text.tertiary.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                        onClick = { bytes ->
                                            onImageClick(image.id, image.path)
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Reports list
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MedAIText(
                                text = "ANALYTICAL REPORTS",
                                style = MedAITheme.textStyle.label.small.copy(fontWeight = FontWeight.Bold),
                                color = MedAITheme.colors.text.secondary
                            )
                            if (currentUserRole == UserRole.SECRETARY) {
                                TextButton(
                                    onClick = onUploadReportClick,
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Report", style = MedAITheme.textStyle.label.small)
                                }
                            }
                        }

                        if (reportsLoading) {
                            Box(modifier = Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MedAITheme.colors.primary)
                            }
                        } else if (reports.isEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.background.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                MedAIText(
                                    text = "No diagnostic reports generated yet for this scan.",
                                    style = MedAITheme.textStyle.body.small,
                                    color = MedAITheme.colors.text.secondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                                )
                            }
                        } else {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                reports.forEach { report ->
                                    ReportItem(
                                        report = report,
                                        currentUserRole = currentUserRole,
                                        onViewClick = { onReportClick(report) },
                                        onDeleteClick = { onDeleteReport(report.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ReportItem(
        report: Report,
        currentUserRole: UserRole?,
        onViewClick: () -> Unit,
        onDeleteClick: () -> Unit
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.background),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f).clickable { onViewClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = MedAITheme.colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        MedAIText(
                            text = report.path.split("/").last(),
                            style = MedAITheme.textStyle.body.medium.copy(fontWeight = FontWeight.Medium)
                        )
                        MedAIText(
                            text = "Added: ${report.createdAt.take(10)}",
                            style = MedAITheme.textStyle.body.small,
                            color = MedAITheme.colors.text.secondary
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onViewClick) {
                        Icon(Icons.Default.RemoveRedEye, null, tint = MedAITheme.colors.primary, modifier = Modifier.size(18.dp))
                    }
                    if (currentUserRole == UserRole.SECRETARY) {
                        IconButton(onClick = onDeleteClick) {
                            Icon(Icons.Default.Delete, null, tint = MedAITheme.colors.status.error, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ScanImageThumbnail(
        patientUserId: String,
        imageId: String,
        getScanImageFileUseCase: GetScanImageFileUseCase,
        modifier: Modifier = Modifier,
        onClick: (ByteArray) -> Unit
    ) {
        var bytes by remember { mutableStateOf<ByteArray?>(null) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(imageId) {
            getScanImageFileUseCase(patientUserId, imageId).fold(
                onSuccess = {
                    bytes = it
                    isLoading = false
                },
                onFailure = {
                    isLoading = false
                }
            )
        }

        Box(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0F172A))
                .clickable(enabled = bytes != null) { bytes?.let { onClick(it) } },
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MedAITheme.colors.primary,
                    strokeWidth = 2.dp
                )
            } else {
                val bitmap = remember(bytes) { bytes?.toImageBitmap() }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Scan Plate Thumbnail",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.BrokenImage,
                        contentDescription = "Broken image",
                        tint = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
