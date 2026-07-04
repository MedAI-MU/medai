package org.example.project.presentation.secretary.billing

import org.example.project.domain.model.secretary.Invoice

data class BillingState(
    val invoices: List<Invoice> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class BillingEvent {
    object LoadInvoices : BillingEvent()
    data class MarkAsPaid(val invoiceId: String) : BillingEvent()
}

sealed class BillingEffect {
    data class ShowSnackbar(val message: String, val isError: Boolean = false) : BillingEffect()
}
