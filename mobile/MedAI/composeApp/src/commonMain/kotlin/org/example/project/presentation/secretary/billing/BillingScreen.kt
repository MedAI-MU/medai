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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.secretary.Invoice
import org.example.project.design_system.theme.LocalDimensions

class BillingScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<BillingViewModel>()
        val state by viewModel.state.collectAsState()
        val dimensions = LocalDimensions.current

        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(viewModel.effect) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is BillingEffect.ShowSnackbar -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
            }
        }

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
            containerColor = MedAITheme.colors.background,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(dimensions.medium)
            ) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MedAITheme.colors.primary)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(dimensions.small)
                    ) {
                        items(state.invoices) { invoice ->
                            InvoiceItem(
                                invoice = invoice,
                                onMarkPaid = { viewModel.onEvent(BillingEvent.MarkAsPaid(invoice.id)) }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun InvoiceItem(invoice: Invoice, onMarkPaid: () -> Unit) {
        val dimensions = LocalDimensions.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MedAITheme.colors.surface, shape = androidx.compose.foundation.shape.RoundedCornerShape(dimensions.radiusMedium))
                .padding(dimensions.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = invoice.patientName, style = MedAITheme.textStyle.body.large, fontWeight = FontWeight.Bold, color = MedAITheme.colors.text.primary)
                    Text(text = "${invoice.amount}", style = MedAITheme.textStyle.headline.small, color = MedAITheme.colors.primary, fontWeight = FontWeight.Bold)
                    Text(text = invoice.items.joinToString(", "), style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                }

                StatusBadge(invoice.status)
            }

            Spacer(modifier = Modifier.height(dimensions.small))

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
        val dimensions = LocalDimensions.current
        val (color, text) = when (status) {
            "PENDING" -> MedAITheme.colors.status.warning to "Pending"
            "PAID" -> MedAITheme.colors.status.success to "Paid"
            "CANCELLED" -> MedAITheme.colors.status.error to "Cancelled"
            else -> MedAITheme.colors.text.secondary to status
        }

        Box(
            modifier = Modifier
                .background(color.copy(alpha = 0.2f), shape = androidx.compose.foundation.shape.RoundedCornerShape(dimensions.radiusSmall))
                .padding(horizontal = dimensions.small, vertical = dimensions.extraSmall)
        ) {
            Text(text = text, style = MedAITheme.textStyle.label.small, color = color, fontWeight = FontWeight.Bold)
        }
    }
}
