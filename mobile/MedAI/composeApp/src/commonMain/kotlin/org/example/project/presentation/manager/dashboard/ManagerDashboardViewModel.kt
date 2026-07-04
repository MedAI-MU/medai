package org.example.project.presentation.manager.dashboard

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.model.manager.ManagedUser
import org.example.project.domain.usecase.manager.*

class ManagerDashboardViewModel(
    private val getPendingDoctorsUseCase: GetPendingDoctorsUseCase,
    private val getPendingSecretariesUseCase: GetPendingSecretariesUseCase,
    private val getApprovedDoctorsUseCase: GetApprovedDoctorsUseCase,
    private val getApprovedSecretariesUseCase: GetApprovedSecretariesUseCase,
    private val approveDoctorUseCase: ApproveDoctorUseCase,
    private val approveSecretaryUseCase: ApproveSecretaryUseCase,
    private val removeUserUseCase: RemoveUserUseCase
) : MviScreenModel<ManagerDashboardState, ManagerDashboardEvent, ManagerDashboardEffect>(
    initialState = ManagerDashboardState()
) {

    init {
        fetchData()
    }

    override fun onEvent(event: ManagerDashboardEvent) {
        when (event) {
            ManagerDashboardEvent.Refresh -> fetchData()
            is ManagerDashboardEvent.TabSelected -> {
                setState { copy(selectedTab = event.tab) }
            }
            is ManagerDashboardEvent.RoleFilterSelected -> {
                setState { copy(selectedRoleFilter = event.filter) }
            }
            is ManagerDashboardEvent.ApproveUser -> {
                approveUser(event.user)
            }
            is ManagerDashboardEvent.RejectUser -> {
                rejectUser(event.user)
            }
            is ManagerDashboardEvent.RevokeUser -> {
                revokeUser(event.user)
            }
            is ManagerDashboardEvent.UserSelected -> {
                setState { copy(selectedUser = event.user) }
            }
            ManagerDashboardEvent.DismissUserDetail -> {
                setState { copy(selectedUser = null) }
            }
        }
    }

    private fun fetchData() {
        screenModelScope.launch {
            setState { copy(uiState = ManagerUiState.Loading) }

            val pendingDoctorsDeferred = async { getPendingDoctorsUseCase() }
            val pendingSecretariesDeferred = async { getPendingSecretariesUseCase() }
            val approvedDoctorsDeferred = async { getApprovedDoctorsUseCase() }
            val approvedSecretariesDeferred = async { getApprovedSecretariesUseCase() }

            val pendingDoctorsResult = pendingDoctorsDeferred.await()
            val pendingSecretariesResult = pendingSecretariesDeferred.await()
            val approvedDoctorsResult = approvedDoctorsDeferred.await()
            val approvedSecretariesResult = approvedSecretariesDeferred.await()

            if (pendingDoctorsResult.isSuccess && pendingSecretariesResult.isSuccess &&
                approvedDoctorsResult.isSuccess && approvedSecretariesResult.isSuccess
            ) {
                setState {
                    copy(
                        uiState = ManagerUiState.Success,
                        pendingDoctors = pendingDoctorsResult.getOrDefault(emptyList()),
                        pendingSecretaries = pendingSecretariesResult.getOrDefault(emptyList()),
                        approvedDoctors = approvedDoctorsResult.getOrDefault(emptyList()),
                        approvedSecretaries = approvedSecretariesResult.getOrDefault(emptyList())
                    )
                }
            } else {
                val errorMsg = listOfNotNull(
                    pendingDoctorsResult.exceptionOrNull()?.message,
                    pendingSecretariesResult.exceptionOrNull()?.message,
                    approvedDoctorsResult.exceptionOrNull()?.message,
                    approvedSecretariesResult.exceptionOrNull()?.message
                ).joinToString(", ").ifBlank { "Failed to load dashboard data" }

                setState { copy(uiState = ManagerUiState.Error(errorMsg)) }
                sendEffect(ManagerDashboardEffect.ShowSnackbar(errorMsg))
            }
        }
    }

    private fun approveUser(user: ManagedUser) {
        if (state.value.processingUserIds.contains(user.id)) return

        setState { copy(processingUserIds = processingUserIds + user.id) }

        screenModelScope.launch {
            val result = if (user.role.lowercase() == "doctor") {
                approveDoctorUseCase(user.id)
            } else {
                approveSecretaryUseCase(user.id)
            }

            result.fold(
                onSuccess = {
                    sendEffect(ManagerDashboardEffect.ShowSnackbar("Approved ${user.name} successfully"))
                    fetchData()
                },
                onFailure = { error ->
                    sendEffect(ManagerDashboardEffect.ShowSnackbar("Failed to approve ${user.name}: ${error.message}"))
                }
            )
            setState { copy(processingUserIds = processingUserIds - user.id) }
        }
    }

    private fun rejectUser(user: ManagedUser) {
        if (state.value.processingUserIds.contains(user.id)) return

        setState { copy(processingUserIds = processingUserIds + user.id) }

        screenModelScope.launch {
            removeUserUseCase(user.id).fold(
                onSuccess = {
                    sendEffect(ManagerDashboardEffect.ShowSnackbar("Rejected ${user.name} successfully"))
                    fetchData()
                },
                onFailure = { error ->
                    sendEffect(ManagerDashboardEffect.ShowSnackbar("Failed to reject ${user.name}: ${error.message}"))
                }
            )
            setState { copy(processingUserIds = processingUserIds - user.id) }
        }
    }

    private fun revokeUser(user: ManagedUser) {
        if (state.value.processingUserIds.contains(user.id)) return

        setState { copy(processingUserIds = processingUserIds + user.id) }

        screenModelScope.launch {
            removeUserUseCase(user.id).fold(
                onSuccess = {
                    sendEffect(ManagerDashboardEffect.ShowSnackbar("Revoked access for ${user.name} successfully"))
                    fetchData()
                },
                onFailure = { error ->
                    sendEffect(ManagerDashboardEffect.ShowSnackbar("Failed to revoke access: ${error.message}"))
                }
            )
            setState { copy(processingUserIds = processingUserIds - user.id) }
        }
    }
}
