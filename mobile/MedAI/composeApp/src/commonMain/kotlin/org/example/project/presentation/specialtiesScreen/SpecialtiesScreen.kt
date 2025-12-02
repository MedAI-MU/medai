package org.example.project.presentation.specialtiesScreen

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.collectLatest
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.doctors_label
import medai.composeapp.generated.resources.find_your_doctor
import medai.composeapp.generated.resources.search_placeholder
import medai.composeapp.generated.resources.sort_by
import medai.composeapp.generated.resources.specialties
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.component.textFields.MedAISearchBar
import org.example.project.design_system.theme.MedAITheme
import org.example.project.presentation.homeScreen.component.SpecialtyItem
import org.jetbrains.compose.resources.stringResource

class SpecialtiesScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<SpecialtiesViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }


        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                when(effect) {
                    SpecialtiesEffect.NavigateBack -> navigator.pop()
                    is SpecialtiesEffect.NavigateToDoctorsBySpecialty -> {
                        // navigator.push(DoctorsListScreen(effect.specialtyId))
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
                        // We can build a custom header here inside the scaffold.
                        .padding(horizontal = 24.dp)
                ) {
                    MedAIText(
                        text = stringResource(Res.string.find_your_doctor),
                        style = MedAITheme.textStyle.body.large,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Search Bar
                    MedAISearchBar(
                        query = state.searchQuery,
                        onQueryChange = { viewModel.onEvent(SpecialtiesEvent.SearchQueryChanged(it)) },
                        placeholder = stringResource(Res.string.search_placeholder)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // --- White Content Area (Rounded Top) ---
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(MedAITheme.colors.background)
                        .padding(24.dp)
                ) {
                    Column {
                        // Filters Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MedAIText(text = stringResource(Res.string.sort_by), color = MedAITheme.colors.text.secondary,style = MedAITheme.textStyle.label.medium)

                                // Simple Sort Chip
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .clickable { viewModel.onEvent(SpecialtiesEvent.SortClicked) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    MedAIText(
                                        text = state.sortOption.name,
                                        color = Color.White,
                                        style = MedAITheme.textStyle.label.small
                                    )
                                }
                            }

                            MedAIText(
                                text = stringResource(Res.string.doctors_label),
                                color = Color(0xFF00E5FF),
                                style = MedAITheme.textStyle.label.large.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        if (state.isLoading) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = MedAITheme.colors.primary)
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(bottom = 24.dp)
                            ) {
                                items(state.filteredSpecialties) { specialty ->
                                    SpecialtyItem(
                                        specialty = specialty.origin,
                                        onClick = { viewModel.onEvent(SpecialtiesEvent.SpecialtyClicked(specialty.origin.id)) }
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
