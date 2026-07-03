package org.example.project.presentation.pendingApprovalScreen

import org.example.project.domain.model.auth.AccountStatus

data class PendingApprovalState(
    val password: String = "",
    val isCheckingStatus: Boolean = false,
    val currentStatus: AccountStatus = AccountStatus.PENDING,
    val error: String? = null
)

sealed class PendingApprovalEvent {
    data class PasswordChanged(val value: String) : PendingApprovalEvent()
    object CheckStatusClicked : PendingApprovalEvent()
    object LogoutClicked : PendingApprovalEvent()
    object ErrorShown : PendingApprovalEvent()
}

sealed class PendingApprovalEffect {
    object NavigateToHome : PendingApprovalEffect()
    object NavigateToWelcome : PendingApprovalEffect()
    data class ShowError(val message: String) : PendingApprovalEffect()
    object ShowStillPending : PendingApprovalEffect()
}
