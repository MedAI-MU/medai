package org.example.project.presentation.pendingApprovalScreen

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.model.auth.AccountStatus
import org.example.project.domain.usecase.auth.CheckAccountStatusUseCase
import org.example.project.domain.repository.auth.UserSessionManager

class PendingApprovalViewModel(
    private val checkAccountStatusUseCase: CheckAccountStatusUseCase,
    private val sessionManager: UserSessionManager
) : MviScreenModel<PendingApprovalState, PendingApprovalEvent, PendingApprovalEffect>(
    PendingApprovalState()
) {
    override fun onEvent(event: PendingApprovalEvent) {
        when (event) {
            is PendingApprovalEvent.PasswordChanged -> setState { copy(password = event.value, error = null) }
            is PendingApprovalEvent.CheckStatusClicked -> checkStatus()
            is PendingApprovalEvent.LogoutClicked -> logout()
            is PendingApprovalEvent.ErrorShown -> setState { copy(error = null) }
        }
    }

    private fun checkStatus() {
        val currentState = state.value
        if (currentState.isCheckingStatus) return
        if (currentState.password.isBlank()) {
            setState { copy(error = "Please enter your password to check status") }
            return
        }

        setState { copy(isCheckingStatus = true, error = null) }

        screenModelScope.launch {
            val result = checkAccountStatusUseCase(currentState.password)
            result.fold(
                onSuccess = { status ->
                    setState { copy(isCheckingStatus = false, currentStatus = status, password = "") }
                    when (status) {
                        AccountStatus.APPROVED -> sendEffect(PendingApprovalEffect.NavigateToHome)
                        AccountStatus.PENDING -> sendEffect(PendingApprovalEffect.ShowStillPending)
                    }
                },
                onFailure = { error ->
                    setState { copy(isCheckingStatus = false, error = error.message) }
                    sendEffect(PendingApprovalEffect.ShowError(
                        error.message ?: "Failed to check status"
                    ))
                }
            )
        }
    }

    private fun logout() {
        screenModelScope.launch {
            sessionManager.clearSession()
            sendEffect(PendingApprovalEffect.NavigateToWelcome)
        }
    }
}
