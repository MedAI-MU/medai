package org.example.project.domain.usecase.manager

import org.example.project.domain.model.manager.ManagedUser
import org.example.project.domain.repository.manager.ManagerRepository

class GetPendingDoctorsUseCase(private val repository: ManagerRepository) {
    suspend operator fun invoke(): Result<List<ManagedUser>> = repository.getPendingDoctors()
}

class GetPendingSecretariesUseCase(private val repository: ManagerRepository) {
    suspend operator fun invoke(): Result<List<ManagedUser>> = repository.getPendingSecretaries()
}

class GetApprovedDoctorsUseCase(private val repository: ManagerRepository) {
    suspend operator fun invoke(): Result<List<ManagedUser>> = repository.getApprovedDoctors()
}

class GetApprovedSecretariesUseCase(private val repository: ManagerRepository) {
    suspend operator fun invoke(): Result<List<ManagedUser>> = repository.getApprovedSecretaries()
}

class ApproveDoctorUseCase(private val repository: ManagerRepository) {
    suspend operator fun invoke(userId: Int): Result<Unit> = repository.approveDoctor(userId)
}

class ApproveSecretaryUseCase(private val repository: ManagerRepository) {
    suspend operator fun invoke(userId: Int): Result<Unit> = repository.approveSecretary(userId)
}

class RemoveUserUseCase(private val repository: ManagerRepository) {
    suspend operator fun invoke(userId: Int): Result<Unit> = repository.removeUser(userId)
}
