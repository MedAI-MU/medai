package org.example.project.presentation.doctor.prescription

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.flow.collectLatest
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.textFields.MedAiTextField
import org.example.project.design_system.theme.MedAITheme

class EPrescriptionScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val viewModel = getScreenModel<EPrescriptionViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                when (effect) {
                    is EPrescriptionEffect.NavigateBack -> navigator?.pop()
                    is EPrescriptionEffect.ShowSuccess -> snackbarHostState.showSnackbar(effect.message)
                    is EPrescriptionEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        MedAIScaffold(
            title = "New Prescription",
            onBackClick = { navigator?.pop() },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Patient Information",
                        style = MedAITheme.textStyle.headline.small,
                        color = MedAITheme.colors.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    MedAiTextField(
                        value = state.patientName,
                        onValueChange = { viewModel.onEvent(EPrescriptionEvent.PatientNameChanged(it)) },
                        placeholder = "Patient Name",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Prescription Details",
                        style = MedAITheme.textStyle.headline.small,
                        color = MedAITheme.colors.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    MedAiTextField(
                        value = state.medicationName,
                        onValueChange = { viewModel.onEvent(EPrescriptionEvent.MedicationNameChanged(it)) },
                        placeholder = "Medication Name",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    MedAiTextField(
                        value = state.dosage,
                        onValueChange = { viewModel.onEvent(EPrescriptionEvent.DosageChanged(it)) },
                        placeholder = "Dosage (e.g. 500mg)",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    MedAiTextField(
                        value = state.instructions,
                        onValueChange = { viewModel.onEvent(EPrescriptionEvent.InstructionsChanged(it)) },
                        placeholder = "Instructions (e.g. twice daily)",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    MedAIButton(
                        text = "Send Prescription",
                        onClick = { viewModel.onEvent(EPrescriptionEvent.SubmitClicked) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
