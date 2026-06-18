package org.example.project.presentation.doctor.services.xray

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.core.presentation.image.toImageBitmap
import org.example.project.design_system.component.appBar.MedAiAppBar
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.theme.MedAITheme
import org.example.project.presentation.doctor.services.DoctorServicesScreen

class XRayResultsScreen(
    private val predictions: List<Pair<String, Float>>,
    private val imageBytes: ByteArray
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val imageBitmap = remember(imageBytes) { imageBytes.toImageBitmap() }

        MedAIScaffold(
            topBar = {
                MedAiAppBar(
                    title = "Analysis Report",
                    centerTitle = true,
                    onBackClick = { navigator.pop() }
                )
            },
            contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MedAITheme.colors.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Done Header Banner Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00C853).copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF00C853),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Scan Complete",
                                style = MedAITheme.textStyle.title.medium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00C853)
                            )
                            Text(
                                text = "On-device diagnostic model completed scanning 14 pathologies.",
                                style = MedAITheme.textStyle.body.small,
                                color = MedAITheme.colors.text.secondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // X-Ray mini card preview
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = "Mini X-Ray Plate",
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Analyzed Specimen",
                                style = MedAITheme.textStyle.title.small,
                                fontWeight = FontWeight.Bold,
                                color = MedAITheme.colors.text.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Target: Frontal Thorax Plate\nPreprocessing: Standardised (320x320 px)",
                                style = MedAITheme.textStyle.body.small,
                                color = MedAITheme.colors.text.secondary,
                                lineHeight = MedAITheme.textStyle.body.small.fontSize * 1.3
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Diagnostic Breakdown Header
                Text(
                    text = "PATHOLOGICAL RISKS BREAKDOWN",
                    style = MedAITheme.textStyle.label.medium,
                    color = MedAITheme.colors.text.secondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Pathology Cards List
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        predictions.forEach { (pathology, probability) ->
                            PathologyResultRow(
                                pathologyName = pathology,
                                probability = probability
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Back Button
                Button(
                    onClick = { navigator.popUntil { it is DoctorServicesScreen } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MedAITheme.colors.primary
                    )
                ) {
                    Text(
                        text = "Complete Diagnostic Session",
                        style = MedAITheme.textStyle.title.small,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun PathologyResultRow(
    pathologyName: String,
    probability: Float
) {
    val percentage = (probability * 100).toInt()

    // Custom color coding logic
    val (riskText, riskColor) = when {
        probability >= 0.50f -> Pair("High Risk", Color(0xFFD32F2F)) // Deep Crimson Red
        probability >= 0.10f -> Pair("Moderate Risk", Color(0xFFF57C00)) // Deep Amber Orange
        else -> Pair("Negligible", Color(0xFF388E3C)) // Emerald Green
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = pathologyName,
                style = MedAITheme.textStyle.body.large,
                fontWeight = FontWeight.SemiBold,
                color = MedAITheme.colors.text.primary
            )

            // Risk Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(riskColor.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "$riskText ($percentage%)",
                    style = MedAITheme.textStyle.label.small,
                    color = riskColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Progress Bar representing percentage
        LinearProgressIndicator(
            progress = probability,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = riskColor,
            trackColor = riskColor.copy(alpha = 0.15f),
            strokeCap = StrokeCap.Round
        )
    }
}
