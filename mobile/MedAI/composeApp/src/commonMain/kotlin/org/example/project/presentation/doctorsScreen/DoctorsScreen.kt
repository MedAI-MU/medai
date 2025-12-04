package org.example.project.presentation.doctorsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.collectLatest
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.cardiology_label
import medai.composeapp.generated.resources.filter
import medai.composeapp.generated.resources.find_your_doctor_subtitle
import medai.composeapp.generated.resources.search_placeholder
import medai.composeapp.generated.resources.see_all
import medai.composeapp.generated.resources.sort_az
import medai.composeapp.generated.resources.sort_by
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.component.textFields.MedAiTextField
import org.example.project.design_system.theme.MedAITheme
import org.example.project.presentation.doctorsScreen.component.DoctorCard
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

class DoctorsScreen(
    private val specialtyId: String? = null,
    private val specialtyName: String?
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<DoctorsListViewModel> { parametersOf(specialtyId) }
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                when(effect) {
                    DoctorsListEffect.NavigateBack -> navigator.pop()
                    is DoctorsListEffect.NavigateToDoctorDetails -> {
                        // navigator.push(DoctorDetailsScreen(effect.doctorId))
                    }
                    is DoctorsListEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
        val screenTitle = specialtyName ?: stringResource(Res.string.find_your_doctor_subtitle)
        MedAIScaffold(
            title = screenTitle,
            onBackClick = { viewModel.onEvent(DoctorsListEvent.BackClicked) },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Header Content
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                ) {
                    MedAIText(
                        text = stringResource(Res.string.find_your_doctor_subtitle),
                        style = MedAITheme.textStyle.body.large,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    MedAiTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.onEvent(DoctorsListEvent.SearchQueryChanged(it)) },
                        placeholder = stringResource(Res.string.search_placeholder)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // White Body with Rounded Top
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(MedAITheme.colors.background)
                        .padding(24.dp)
                ) {
                    Column {
                        // Filters
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MedAIText(
                                text = stringResource(Res.string.sort_by),
                                style = MedAITheme.textStyle.label.medium,
                                color = MedAITheme.colors.text.secondary,
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFF00E5FF))
                                    .clickable { viewModel.onEvent(DoctorsListEvent.SortClicked) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                MedAIText(text = stringResource(Res.string.sort_az), color = Color.White, style = MedAITheme.textStyle.label.small)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(50))
                                    .clickable { viewModel.onEvent(DoctorsListEvent.FilterClicked) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                MedAIText(text = stringResource(Res.string.filter), color = Color(0xFF00E5FF), style = MedAITheme.textStyle.label.small)
                            }

                            Spacer(modifier = Modifier.weight(1f))
                            MedAIText(text = stringResource(Res.string.see_all), color = Color(0xFF00E5FF), style = MedAITheme.textStyle.label.medium)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (state.isLoading) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = MedAITheme.colors.primary)
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(bottom = 24.dp)
                            ) {
                                items(state.filteredDoctors) { doctor ->
                                    DoctorCard(
                                        doctor = doctor,
                                        onClick = { viewModel.onEvent(DoctorsListEvent.DoctorClicked(doctor.id)) }
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
