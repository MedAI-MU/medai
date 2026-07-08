package org.example.project.presentation.manager.dashboard

import org.example.project.domain.model.manager.ManagedUser

data class ManagerDashboardState(
    val uiState: ManagerUiState = ManagerUiState.Loading,
    val selectedTab: ManagerTab = ManagerTab.PENDING,
    val selectedRoleFilter: ManagerRoleFilter = ManagerRoleFilter.DOCTORS,
    val pendingDoctors: List<ManagedUser> = emptyList(),
    val pendingSecretaries: List<ManagedUser> = emptyList(),
    val approvedDoctors: List<ManagedUser> = emptyList(),
    val approvedSecretaries: List<ManagedUser> = emptyList(),
    val processingUserIds: Set<Int> = emptySet(),
    val selectedUser: ManagedUser? = null
)

enum class ManagerTab {
    PENDING, APPROVED
}

enum class ManagerRoleFilter {
    DOCTORS, SECRETARIES
}

sealed class ManagerUiState {
    object Loading : ManagerUiState()
    object Success : ManagerUiState()
    data class Error(val message: String) : ManagerUiState()
}

sealed class ManagerDashboardEvent {
    object Refresh : ManagerDashboardEvent()
    data class TabSelected(val tab: ManagerTab) : ManagerDashboardEvent()
    data class RoleFilterSelected(val filter: ManagerRoleFilter) : ManagerDashboardEvent()
    data class ApproveUser(val user: ManagedUser) : ManagerDashboardEvent()
    data class RejectUser(val user: ManagedUser) : ManagerDashboardEvent()
    data class RevokeUser(val user: ManagedUser) : ManagerDashboardEvent()
    data class UserSelected(val user: ManagedUser) : ManagerDashboardEvent()
    object DismissUserDetail : ManagerDashboardEvent()
}

sealed class ManagerDashboardEffect {
    data class ShowSnackbar(val message: String) : ManagerDashboardEffect()
}
