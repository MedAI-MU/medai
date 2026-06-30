package org.example.project.di

import org.example.project.data.repository.NetworkPatientRepository
import org.example.project.data.repository.mock.MockMedicalRecordRepository
import org.example.project.domain.repository.medical_record.MedicalRecordRepository
import org.example.project.domain.repository.patient.PatientRepository
import org.example.project.domain.usecase.medical_record.*
import org.example.project.domain.usecase.patient.*
import org.example.project.presentation.patientDirectory.PatientsDirectoryViewModel
import org.example.project.presentation.shared.records.SharedMedicalRecordViewModel
import org.koin.dsl.module

val medicalRecordModule = module {
    single<MedicalRecordRepository> { MockMedicalRecordRepository() }
    single<PatientRepository> { NetworkPatientRepository(get()) }

    // Patient Use Cases
    factory { GetPatientsUseCase(get()) }
    factory { GetPatientByIdUseCase(get()) }
    factory { UpdatePatientUseCase(get()) }

    // Medical Record Use Cases
    factory { AddAllergyUseCase(get()) }
    factory { UpdateAllergyUseCase(get()) }
    factory { DeleteAllergyUseCase(get()) }
    factory { AddChronicDiseaseUseCase(get()) }
    factory { UpdateChronicDiseaseUseCase(get()) }
    factory { DeleteChronicDiseaseUseCase(get()) }
    factory { AddSurgeryUseCase(get()) }
    factory { UpdateSurgeryUseCase(get()) }
    factory { DeleteSurgeryUseCase(get()) }
    factory { AddFamilyHistoryUseCase(get()) }
    factory { UpdateFamilyHistoryUseCase(get()) }
    factory { DeleteFamilyHistoryUseCase(get()) }
    factory { AddEmergencyContactUseCase(get()) }
    factory { UpdateEmergencyContactUseCase(get()) }
    factory { DeleteEmergencyContactUseCase(get()) }

    factory { GetAllergiesUseCase(get()) }
    factory { GetAnalysesUseCase(get()) }
    factory { GetVaccinationsUseCase(get()) }
    factory { GetMedicalHistoryUseCase(get()) }
    factory { GetAnalysisDetailsUseCase(get()) }
    factory { GetPatientProfileUseCase(get()) }
    factory { UpdatePatientMetricsUseCase(get()) }

    factory { PatientsDirectoryViewModel(get()) }
    factory { params ->
        val patientId = params.getOrNull<String>()
        SharedMedicalRecordViewModel(
            patientIdArg = patientId,
            patientRepository = get(),
            addAllergyUseCase = get(),
            updateAllergyUseCase = get(),
            deleteAllergyUseCase = get(),
            addChronicDiseaseUseCase = get(),
            updateChronicDiseaseUseCase = get(),
            deleteChronicDiseaseUseCase = get(),
            addSurgeryUseCase = get(),
            updateSurgeryUseCase = get(),
            deleteSurgeryUseCase = get(),
            addFamilyHistoryUseCase = get(),
            updateFamilyHistoryUseCase = get(),
            deleteFamilyHistoryUseCase = get(),
            addEmergencyContactUseCase = get(),
            updateEmergencyContactUseCase = get(),
            deleteEmergencyContactUseCase = get(),
            sessionManager = get()
        )
    }
}
