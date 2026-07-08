package org.example.project.domain.repository.manager

import org.example.project.domain.model.manager.ManagedUser

interface ManagerRepository {
    suspend fun getPendingDoctors(): Result<List<ManagedUser>>
    suspend fun getPendingSecretaries(): Result<List<ManagedUser>>
    suspend fun getApprovedDoctors(): Result<List<ManagedUser>>
    suspend fun getApprovedSecretaries(): Result<List<ManagedUser>>
    suspend fun approveDoctor(userId: Int): Result<Unit>
    suspend fun approveSecretary(userId: Int): Result<Unit>
    suspend fun removeUser(userId: Int): Result<Unit>
}
