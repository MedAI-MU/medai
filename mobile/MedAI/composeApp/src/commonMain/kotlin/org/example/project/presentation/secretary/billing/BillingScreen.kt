package org.example.project.presentation.secretary.billing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.secretary.Invoice
import org.example.project.domain.usecase.secretary.GenerateInvoiceUseCase
import org.example.project.domain.usecase.secretary.GetDashboardStatsUseCase // Reusing for stats, but better to have GetInvoicesUseCase
import org.example.project.domain.repository.SecretaryRepository // Accessing directly for MVP specific query or add usecase

class BillingScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<BillingViewModel>()
        val state by viewModel.state.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Billing & Invoices", style = MedAITheme.textStyle.headline.small) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MedAITheme.colors.primary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MedAITheme.colors.background,
                        titleContentColor = MedAITheme.colors.text.primary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { /* TODO: New Invoice Dialog */ },
                    containerColor = MedAITheme.colors.primary,
                    contentColor = MedAITheme.colors.text.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Invoice")
                }
            },
            containerColor = MedAITheme.colors.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MedAITheme.colors.primary)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.invoices) { invoice ->
                            InvoiceItem(
                                invoice = invoice,
                                onMarkPaid = { viewModel.markAsPaid(invoice.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun InvoiceItem(invoice: Invoice, onMarkPaid: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MedAITheme.colors.surface, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = invoice.patientName, style = MedAITheme.textStyle.body.large, fontWeight = FontWeight.Bold, color = MedAITheme.colors.text.primary)
                    Text(text = "$${invoice.amount}", style = MedAITheme.textStyle.headline.small, color = MedAITheme.colors.primary, fontWeight = FontWeight.Bold)
                    Text(text = invoice.items.joinToString(", "), style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                }

                StatusBadge(invoice.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (invoice.status == "PENDING") {
                MedAIButton(
                    text = "Mark as Paid",
                    onClick = onMarkPaid,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Composable
    fun StatusBadge(status: String) {
        val (color, text) = when (status) {
            "PENDING" -> Color(0xFFFFCC00) to "Pending"
            "PAID" -> Color(0xFF00C853) to "Paid"
            "CANCELLED" -> Color(0xFFFF5252) to "Cancelled"
            else -> MedAITheme.colors.text.secondary to status
        }

        Box(
            modifier = Modifier
                .background(color.copy(alpha = 0.2f), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = text, style = MedAITheme.textStyle.label.small, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

class BillingViewModel(
    private val repository: SecretaryRepository // Using repo directly for MVP query simplicity (getPendingInvoices not wrapped in UC yet)
) : ScreenModel {

    data class State(
        val invoices: List<Invoice> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        loadInvoices()
    }

    private fun loadInvoices() {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // Fetching pending invoices. In real app, we might want all.
                // Assuming repo has a method for this or we filter locally. The interface has `getPendingInvoices`.
                // Let's assume we want ALL invoices for history? The repo interface only has `getPendingInvoices`.
                // I'll stick to pending for now or check repo.
                // Ah, MockSecretaryRepositoryImpl has `getPendingInvoices`.
                val invoices = repository.getPendingInvoices()
                _state.value = _state.value.copy(invoices = invoices, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun markAsPaid(invoiceId: String) {
        screenModelScope.launch {
            try {
                repository.markInvoiceAsPaid(invoiceId)
                loadInvoices() // Refresh
            } catch (e: Exception) {
                // handle error
            }
        }
    }
}
