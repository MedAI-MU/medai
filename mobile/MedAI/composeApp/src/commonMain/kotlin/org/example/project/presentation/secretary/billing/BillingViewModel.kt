package org.example.project.presentation.secretary.billing

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.repository.secretary.SecretaryRepository

class BillingViewModel(
    private val repository: SecretaryRepository
) : MviScreenModel<BillingState, BillingEvent, BillingEffect>(BillingState()) {

    init {
        onEvent(BillingEvent.LoadInvoices)
    }

    override fun onEvent(event: BillingEvent) {
        when (event) {
            is BillingEvent.LoadInvoices -> loadInvoices()
            is BillingEvent.MarkAsPaid -> markAsPaid(event.invoiceId)
        }
    }

    private fun loadInvoices() {
        screenModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                val invoices = repository.getPendingInvoices()
                setState { copy(invoices = invoices, isLoading = false) }
            } catch (e: Exception) {
                setState { copy(error = e.message, isLoading = false) }
                sendEffect(BillingEffect.ShowSnackbar(e.message ?: "Failed to load invoices", isError = true))
            }
        }
    }

    private fun markAsPaid(invoiceId: String) {
        screenModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                repository.markInvoiceAsPaid(invoiceId)
                sendEffect(BillingEffect.ShowSnackbar("Invoice marked as paid"))
                loadInvoices() // Refresh
            } catch (e: Exception) {
                setState { copy(isLoading = false) }
                sendEffect(BillingEffect.ShowSnackbar(e.message ?: "Failed to mark as paid", isError = true))
            }
        }
    }
}
