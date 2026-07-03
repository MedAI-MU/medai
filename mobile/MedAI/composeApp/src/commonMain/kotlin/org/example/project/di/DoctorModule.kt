package org.example.project.di

import org.example.project.data.repository.NetworkDoctorRepository
import org.example.project.data.repository.NetworkSpecialtiesRepository
import org.example.project.domain.repository.doctor.DoctorRepository
import org.example.project.domain.repository.specialty.SpecialtiesRepository
import org.example.project.domain.usecase.doctor.GetDoctorDetailsUseCase
import org.example.project.domain.usecase.doctor.GetDoctorsUseCase
import org.example.project.domain.usecase.specialty.GetSpecialtiesUseCase
import org.example.project.presentation.doctor.dashboard.DoctorDashboardViewModel
import org.example.project.presentation.doctor.prescription.EPrescriptionViewModel

import org.example.project.presentation.doctor.services.xray.XRayAnalysisViewModel
import org.example.project.presentation.doctorDetailsScreen.DoctorDetailsViewModel
import org.example.project.presentation.doctorsScreen.DoctorsListViewModel
import org.example.project.presentation.specialtiesScreen.SpecialtiesViewModel
import org.example.project.domain.usecase.doctor.GetAllSpecialitiesUseCase
import org.example.project.domain.usecase.doctor.AssignSpecialityUseCase
import org.example.project.domain.usecase.doctor.RemoveSpecialityUseCase
import org.example.project.domain.usecase.doctor.CreateSpecialityUseCase
import org.koin.dsl.module

val doctorModule = module {
    single<DoctorRepository> { NetworkDoctorRepository(get()) }
    single<SpecialtiesRepository> { NetworkSpecialtiesRepository(client = get()) }

    factory { GetDoctorsUseCase(get()) }
    factory { GetDoctorDetailsUseCase(get()) }
    factory { GetSpecialtiesUseCase(get()) }
    factory { GetAllSpecialitiesUseCase(get()) }
    factory { AssignSpecialityUseCase(get()) }
    factory { RemoveSpecialityUseCase(get()) }
    factory { CreateSpecialityUseCase(get()) }

    factory { SpecialtiesViewModel(get(), get()) }
    factory { (specialtyId: String?) ->
        DoctorsListViewModel(get(), specialtyId)
    }
    factory { (doctorId: String) ->
        DoctorDetailsViewModel(doctorId, get())
    }

    factory { DoctorDashboardViewModel(get(), get()) }
    factory { XRayAnalysisViewModel(get()) }
    factory { EPrescriptionViewModel() }


}
