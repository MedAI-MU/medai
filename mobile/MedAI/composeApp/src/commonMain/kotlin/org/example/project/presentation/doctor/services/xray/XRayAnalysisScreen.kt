package org.example.project.presentation.doctor.services.xray

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.core.presentation.image.rememberImagePicker
import org.example.project.core.presentation.image.toImageBitmap
import org.example.project.design_system.component.appBar.MedAiAppBar
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.theme.MedAITheme

class XRayAnalysisScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<XRayAnalysisViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
        val imagePicker = rememberImagePicker { bytes ->
            selectedImageBytes = bytes
            viewModel.resetState()
        }

        // Navigate to results screen when successfully analyzed
        LaunchedEffect(uiState) {
            val currentState = uiState
            if (currentState is XRayAnalysisUiState.Success && selectedImageBytes != null) {
                navigator.push(
                    XRayResultsScreen(
                        predictions = currentState.predictions,
                        imageBytes = selectedImageBytes!!
                    )
                )
            }
        }

        MedAIScaffold(
            topBar = {
                MedAiAppBar(
                    title = "X-Ray Scanner",
                    centerTitle = true,
                    onBackClick = { navigator.pop() }
                )
            },
            contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MedAITheme.colors.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Advisory Warning Info Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MedAITheme.colors.primary.copy(alpha = 0.08f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MedAITheme.colors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "This analysis runs 100% on-device. No patient data or medical images are sent to any external server.",
                                style = MedAITheme.textStyle.body.small,
                                color = MedAITheme.colors.text.secondary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Diagnostic Viewer Frame
                    Text(
                        text = "DIAGNOSTIC PLATE VIEWER",
                        style = MedAITheme.textStyle.label.medium,
                        color = MedAITheme.colors.text.secondary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF0F172A)) // Dark radiological screen background
                            .border(
                                width = 1.5.dp,
                                color = if (selectedImageBytes != null) MedAITheme.colors.primary.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                if (uiState !is XRayAnalysisUiState.Loading) {
                                    imagePicker.launch()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val bytes = selectedImageBytes
                        if (bytes != null) {
                            val bitmap = remember(bytes) { bytes.toImageBitmap() }
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Chest X-Ray Plate",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )

                            // Edit overlay badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoLibrary,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Change Plate",
                                        style = MedAITheme.textStyle.label.small,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            // Empty placeholder state
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White.copy(alpha = 0.08f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudUpload,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Upload Chest X-Ray Plate",
                                    style = MedAITheme.textStyle.title.medium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Supported formats: JPEG, PNG. Image will be preprocessed automatically.",
                                    style = MedAITheme.textStyle.body.small,
                                    color = Color.White.copy(alpha = 0.5f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Buttons/Actions
                    selectedImageBytes?.let { bytes ->
                        if (uiState !is XRayAnalysisUiState.Loading) {
                            Button(
                                onClick = { viewModel.analyzeImage(bytes) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MedAITheme.colors.primary
                                )
                            ) {
                                Text(
                                    text = "Start AI Diagnostic Scan",
                                    style = MedAITheme.textStyle.title.small,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Loading overlay
                val state = uiState
                AnimatedVisibility(
                    visible = state is XRayAnalysisUiState.Loading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    if (state is XRayAnalysisUiState.Loading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.75f))
                                .clickable(enabled = false) {}, // Intercept clicks
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = MedAITheme.colors.primary,
                                    strokeWidth = 4.dp,
                                    modifier = Modifier.size(54.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "ANALYZING IMAGES",
                                    style = MedAITheme.textStyle.label.medium,
                                    color = MedAITheme.colors.primary,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = state.message,
                                    style = MedAITheme.textStyle.body.medium,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Error State Overlay
                val errorState = uiState
                AnimatedVisibility(
                    visible = errorState is XRayAnalysisUiState.Error,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    if (errorState is XRayAnalysisUiState.Error) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.8f))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Scan Failed",
                                        style = MedAITheme.textStyle.title.large,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = errorState.message,
                                        style = MedAITheme.textStyle.body.medium,
                                        color = MedAITheme.colors.text.secondary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Button(
                                        onClick = { viewModel.resetState() },
                                        colors = ButtonDefaults.buttonColors(containerColor = MedAITheme.colors.primary)
                                    ) {
                                        Text("Dismiss", color = Color.White)
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
