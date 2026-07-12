package org.example.project.presentation.reportAnalysis

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.LocalDimensions
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.report_analysis.ReportAnalysis
import org.example.project.domain.model.report_analysis.KeyFinding

class ReportAnalysisResultsScreen(private val report: ReportAnalysis) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val dimensions = LocalDimensions.current
        val result = report.analysisResult

        MedAIScaffold(
            title = "AI Analysis Insights",
            onBackClick = { navigator.pop() }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MedAITheme.colors.background)
            ) {
                if (result == null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Info, null, modifier = Modifier.size(64.dp), tint = MedAITheme.colors.text.tertiary)
                        Spacer(modifier = Modifier.height(16.dp))
                        MedAIText("No insights generated for this report.", style = MedAITheme.textStyle.title.medium)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // 1. Patient Metadata Card (English/Standard)
                        result.patientMetadata?.let { meta ->
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        MedAIText(
                                            text = "PATIENT & REPORT DETAILS",
                                            style = MedAITheme.textStyle.label.small.copy(fontWeight = FontWeight.Bold),
                                            color = MedAITheme.colors.text.secondary
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            MetaItem(label = "Name", value = meta.name ?: "Unknown")
                                            MetaItem(label = "Age", value = meta.age ?: "N/A")
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            MetaItem(label = "Gender", value = meta.gender ?: "N/A")
                                            MetaItem(label = "Report Date", value = meta.reportDate ?: "N/A")
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Arabic Summary (RTL Force)
                        result.overallSummaryAr?.let { summary ->
                            item {
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.primary.copy(alpha = 0.08f)),
                                        border = BorderStroke(1.dp, MedAITheme.colors.primary.copy(alpha = 0.2f)),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            MedAIText(
                                                text = "الملخص الطبي العام",
                                                style = MedAITheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold),
                                                color = MedAITheme.colors.primary,
                                                textAlign = TextAlign.Start
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = summary,
                                                style = MedAITheme.textStyle.body.medium.copy(
                                                    lineHeight = 22.sp,
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                color = MedAITheme.colors.text.primary,
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Key Findings Header
                        if (result.keyFindings.isNotEmpty()) {
                            item {
                                MedAIText(
                                    text = "KEY LAB FINDINGS",
                                    style = MedAITheme.textStyle.label.small.copy(fontWeight = FontWeight.Bold),
                                    color = MedAITheme.colors.text.secondary,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            // 4. Key Findings List
                            items(result.keyFindings) { finding ->
                                KeyFindingItemCard(finding = finding)
                            }
                        }

                        // 5. Safety Disclaimer (RTL Force)
                        result.safetyNoteAr?.let { safety ->
                            item {
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.status.warningContainer.copy(alpha = 0.2f)),
                                        border = BorderStroke(1.dp, MedAITheme.colors.status.warning.copy(alpha = 0.4f)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = null,
                                                tint = MedAITheme.colors.status.warning,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                MedAIText(
                                                    text = "تنبيه طبي هام",
                                                    style = MedAITheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold),
                                                    color = MedAITheme.colors.status.warning,
                                                    textAlign = TextAlign.Start
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = safety,
                                                    style = MedAITheme.textStyle.body.small.copy(lineHeight = 18.sp),
                                                    color = MedAITheme.colors.text.primary,
                                                    textAlign = TextAlign.Start
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Bottom Spacer
                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun RowScope.MetaItem(label: String, value: String) {
        Column(modifier = Modifier.weight(1f)) {
            MedAIText(text = label, style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
            MedAIText(text = value, style = MedAITheme.textStyle.title.small.copy(fontWeight = FontWeight.Bold))
        }
    }

    @Composable
    private fun KeyFindingItemCard(finding: KeyFinding) {
        val isElevated = finding.statusAr.contains("مرتفع") || finding.statusAr.contains("منخفض") || finding.statusAr.contains("غير طبيعي")
        val badgeColor = if (isElevated) MedAITheme.colors.status.error else MedAITheme.colors.status.success
        val badgeBg = badgeColor.copy(alpha = 0.15f)

        Card(
            colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header (English Test Name + Arabic Status Badge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MedAIText(
                        text = finding.testName,
                        style = MedAITheme.textStyle.title.medium.copy(fontWeight = FontWeight.Bold)
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = badgeBg),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = finding.statusAr,
                            color = badgeColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Measured Value vs Reference Range
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MedAITheme.colors.background)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        MedAIText("Result Value", style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                        MedAIText(
                            text = "${finding.measuredValue} ${finding.unit}",
                            style = MedAITheme.textStyle.title.small.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        MedAIText("Reference Range", style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                        MedAIText(
                            text = finding.referenceRange,
                            style = MedAITheme.textStyle.title.small.copy(fontWeight = FontWeight.Bold),
                            color = MedAITheme.colors.text.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Patient Arabic Explanation (RTL)
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        MedAIText(
                            text = "الشرح المبسط:",
                            style = MedAITheme.textStyle.label.small.copy(fontWeight = FontWeight.Bold),
                            color = MedAITheme.colors.primary,
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = finding.patientExplanationAr,
                            style = MedAITheme.textStyle.body.medium.copy(lineHeight = 20.sp),
                            color = MedAITheme.colors.text.secondary,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
