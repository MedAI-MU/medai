package org.example.project.presentation.specialtiesScreen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.doctors_label
import medai.composeapp.generated.resources.find_your_doctor
import medai.composeapp.generated.resources.search_placeholder
import medai.composeapp.generated.resources.sort_by
import medai.composeapp.generated.resources.specialties
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.component.textFields.MedAISearchBar
import org.example.project.design_system.theme.LocalDimensions
import org.example.project.design_system.theme.MedAITheme
import org.example.project.presentation.doctorsScreen.DoctorsScreen
import org.example.project.presentation.homeScreen.component.SpecialtyItem
import org.jetbrains.compose.resources.stringResource

class SpecialtiesScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<SpecialtiesViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val dimensions = LocalDimensions.current

        LaunchedEffect(viewModel.effect) {
            viewModel.effect.collect { effect ->
                when(effect) {
                    SpecialtiesEffect.NavigateBack -> navigator.pop()
                    is SpecialtiesEffect.NavigateToDoctorsBySpecialty -> {
                        val screen = DoctorsScreen(specialtyId = effect.specialtyId.name, specialtyName = effect.specialtyId.name)
                        navigator.parent?.push(screen) ?: navigator.push(screen)
                        println("Navigating to Doctors for: ${effect.specialtyId}")
                    }
                    is SpecialtiesEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                    is SpecialtiesEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        MedAIScaffold(
            title = stringResource(Res.string.specialties),
            onBackClick = { viewModel.onEvent(SpecialtiesEvent.BackClicked) },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // --- Header Area ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensions.extraLarge)
                ) {
                    MedAIText(
                        text = stringResource(Res.string.find_your_doctor),
                        style = MedAITheme.textStyle.body.large,
                        color = Color.White.copy(alpha = 0.9f), // Keep white for header text
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(dimensions.extraLarge))

                    // Search Bar
                    MedAISearchBar(
                        query = state.searchQuery,
                        onQueryChange = { viewModel.onEvent(SpecialtiesEvent.SearchQueryChanged(it)) },
                        placeholder = stringResource(Res.string.search_placeholder)
                    )

                    Spacer(modifier = Modifier.height(dimensions.extraLarge))
                }

                // --- White Content Area (Rounded Top) ---
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = dimensions.extraExtraLarge, topEnd = dimensions.extraExtraLarge))
                        .background(MedAITheme.colors.background)
                        .padding(dimensions.extraLarge)
                ) {
                    Column {
                        // Filters Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MedAIText(
                                    text = stringResource(Res.string.sort_by),
                                    color = MedAITheme.colors.text.secondary,
                                    style = MedAITheme.textStyle.label.medium)

                                // Simple Sort Chip
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(dimensions.radiusRound))
                                        .background(MedAITheme.colors.primary)
                                        .clickable { viewModel.onEvent(SpecialtiesEvent.SortClicked) }
                                        .padding(horizontal = dimensions.medium, vertical = dimensions.small)
                                ) {
                                    MedAIText(
                                        text = state.sortOption.name,
                                        color = MedAITheme.colors.text.onPrimary,
                                        style = MedAITheme.textStyle.label.small
                                    )
                                }
                            }

                            MedAIText(
                                text = stringResource(Res.string.doctors_label),
                                color = MedAITheme.colors.primary,
                                style = MedAITheme.textStyle.label.large.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(dimensions.extraLarge))

                        if (state.isLoading) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = MedAITheme.colors.primary)
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                verticalArrangement = Arrangement.spacedBy(dimensions.large),
                                horizontalArrangement = Arrangement.spacedBy(dimensions.large),
                                contentPadding = PaddingValues(bottom = dimensions.extraLarge)
                            ) {
                                items(state.filteredSpecialties) { specialty ->
                                    SpecialtyItem(
                                        specialty = specialty.origin,
                                        onClick = { viewModel.onEvent(SpecialtiesEvent.SpecialtyClicked(specialty)) }
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
