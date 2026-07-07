package org.example.project.data.remote.mapper

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.yearsUntil
import kotlinx.datetime.todayIn
import medai.composeapp.generated.resources.Res
import medai.composeapp.generated.resources.app_name
import medai.composeapp.generated.resources.cat_doctors
import medai.composeapp.generated.resources.cat_favorite
import medai.composeapp.generated.resources.cat_pharmacy
import medai.composeapp.generated.resources.cat_record
import medai.composeapp.generated.resources.cat_specialties
import medai.composeapp.generated.resources.spec_cardiology
import medai.composeapp.generated.resources.spec_dermatology
import medai.composeapp.generated.resources.spec_general
import medai.composeapp.generated.resources.spec_gynecology
import medai.composeapp.generated.resources.spec_odontology
import medai.composeapp.generated.resources.spec_oncology
import org.example.project.data.remote.dto.CategoryDto
import org.example.project.data.remote.dto.NotificationDto
import org.example.project.data.remote.dto.doctor.DoctorResponseDto
import org.example.project.data.remote.dto.patient.PatientResponseDto
import org.example.project.data.remote.dto.patient.AllergyResponseDto
import org.example.project.data.remote.dto.patient.ChronicDiseaseResponseDto
import org.example.project.data.remote.dto.patient.EmergencyContactResponseDto
import org.example.project.data.remote.dto.patient.FamilyHistoryResponseDto
import org.example.project.data.remote.dto.patient.SurgeryResponseDto
import org.example.project.data.remote.dto.patient.CreateAllergyRequestDto
import org.example.project.data.remote.dto.patient.UpdateAllergyRequestDto
import org.example.project.data.remote.dto.patient.CreateChronicDiseaseRequestDto
import org.example.project.data.remote.dto.patient.UpdateChronicDiseaseRequestDto
import org.example.project.data.remote.dto.patient.CreateFamilyHistoryRequestDto
import org.example.project.data.remote.dto.patient.UpdateFamilyHistoryRequestDto
import org.example.project.data.remote.dto.patient.CreateSurgeryRequestDto
import org.example.project.data.remote.dto.patient.UpdateSurgeryRequestDto
import org.example.project.data.remote.dto.patient.CreateEmergencyContactRequestDto
import org.example.project.data.remote.dto.patient.UpdateEmergencyContactRequestDto
import org.example.project.data.remote.dto.patient.CreatePatientRequestDto
import org.example.project.data.remote.dto.patient.UpdatePatientRequestDto
import org.example.project.design_system.icons.MedAIIcons
import org.example.project.domain.model.appointment.AppointmentStatus
import org.example.project.domain.model.home.Category
import org.example.project.domain.model.home.CategoryType
import org.example.project.domain.model.doctor.Doctor
import org.example.project.domain.model.notification.Notification
import org.example.project.domain.model.auth.AccountStatus
import org.example.project.domain.model.patient.Patient
import org.example.project.data.remote.dto.ChatConversationDto
import org.example.project.data.remote.dto.MessageDto
import org.example.project.data.remote.dto.manager.ManagedUserDto
import org.example.project.domain.model.patient.BloodType
import org.example.project.domain.model.chat.ChatConversation
import org.example.project.domain.model.patient.ChronicDiseaseEntity
import org.example.project.domain.model.patient.EmergencyContactEntity
import org.example.project.domain.model.patient.FamilyHistoryEntity
import org.example.project.domain.model.patient.FamilyRelation
import org.example.project.domain.model.patient.Gender
import org.example.project.domain.model.patient.MaritalStatus
import org.example.project.domain.model.chat.Message
import org.example.project.domain.model.chat.MessageStatus
import org.example.project.domain.model.manager.ManagedUser
import org.example.project.domain.model.notification.NotificationType
import org.example.project.domain.model.patient.AllergyEntity
import org.example.project.domain.model.patient.AllergyParams
import org.example.project.domain.model.patient.ChronicDiseaseParams
import org.example.project.domain.model.patient.CreatePatientParams
import org.example.project.domain.model.patient.EmergencyContactParams
import org.example.project.domain.model.patient.FamilyHistoryParams
import org.example.project.domain.model.patient.SurgeryEntity
import org.example.project.domain.model.patient.SurgeryParams
import org.example.project.domain.model.patient.UpdatePatientParams
import org.example.project.data.remote.dto.diagnosis.DiagnosisResponseDto
import org.example.project.data.remote.dto.diagnosis.CreateDiagnosisRequestDto
import org.example.project.data.remote.dto.diagnosis.UpdateDiagnosisRequestDto
import org.example.project.domain.model.diagnosis.Diagnosis
import org.example.project.domain.model.diagnosis.CreateDiagnosisParams
import org.example.project.domain.model.diagnosis.UpdateDiagnosisParams

fun mapStatus(status: String?): AppointmentStatus {
    return when (status?.lowercase()) {
        "pending" -> AppointmentStatus.Pending
        "confirmed" -> AppointmentStatus.Confirmed
        "finished" -> AppointmentStatus.Finished
        "cancelled" -> AppointmentStatus.Cancelled
        else -> AppointmentStatus.Pending // Default fallback
    }
}

fun mapAccountStatus(status: String?): AccountStatus {
    return when (status?.lowercase()) {
        "pending" -> AccountStatus.PENDING
        "approved" -> AccountStatus.APPROVED
        else -> AccountStatus.APPROVED
    }
}

// Maps backend string keys (e.g., "cardiology") to local StringResources
fun mapSpecialtyKeyToRes(key: String) = when (key.lowercase()) {
    "cardiology" -> Res.string.spec_cardiology
    "dermatology" -> Res.string.spec_dermatology
    "gynecology" -> Res.string.spec_gynecology
    "odontology" -> Res.string.spec_odontology
    "oncology" -> Res.string.spec_oncology
    else -> Res.string.spec_general // Default fallback
}

fun mapCategoryKeyToRes(key: String) = when (key.lowercase()) {
    "favorite" -> Res.string.cat_favorite
    "doctors" -> Res.string.cat_doctors
    "pharmacy" -> Res.string.cat_pharmacy
    "specialties" -> Res.string.cat_specialties
    "record" -> Res.string.cat_record
    else -> Res.string.cat_doctors // Default fallback
}

fun mapCategoryDtoToDomain(dto: CategoryDto): Category {
    val categoryType = CategoryType.fromKey(dto.iconKey)

    // Define resources based on the TYPE, not the ID or raw string
    val (titleRes, iconRes) = when (categoryType) {
        CategoryType.DOCTORS -> Res.string.cat_doctors to MedAIIcons.Doctor
        CategoryType.PHARMACY -> Res.string.cat_pharmacy to  MedAIIcons.Pharmacy
        CategoryType.SPECIALTIES -> Res.string.cat_specialties to MedAIIcons.Specialties
        CategoryType.RECORDS -> Res.string.cat_record to  MedAIIcons.Record
        CategoryType.FAVORITE -> Res.string.cat_favorite to  MedAIIcons.Favorites

        CategoryType.UNKNOWN -> Res.string.app_name to  MedAIIcons.Help
    }

    return Category(
        id = dto.id,
        type = categoryType,
        title = titleRes,
        iconName = iconRes
    )
}

 fun DoctorResponseDto.toDomain(): Doctor {
    val primarySpec = this.specialities.find { it.isPrimary }?.speciality?.name
        ?: this.specialities.firstOrNull()?.speciality?.name
        ?: "General"
    return Doctor(
        id = this.userId.toString(),
        name = this.name ?: "Unknown",
        specialty = primarySpec,
        rating = 0.0,
        imageUrl = null,
        bio = "No bio available...",
        reviewCount = 0
    )
 }


fun NotificationDto.toDomain(): Notification {
    return Notification(
        id = this.id,
        title = this.title,
        message = this.body,
        timestamp = parseInstant(this.timestamp),
        type = mapNotificationType(this.type),
        isRead = this.isRead
    )
}

// Helper to map backend strings to Domain Enums
private fun mapNotificationType(type: String): NotificationType {
    return when (type.lowercase()) {
        "appointment_confirmed" -> NotificationType.APPOINTMENT_CONFIRMED
        "appointment_cancelled" -> NotificationType.APPOINTMENT_CANCELLED
        "schedule_changed" -> NotificationType.SCHEDULE_CHANGED
        "general" -> NotificationType.GENERAL_INFO
        else -> NotificationType.GENERAL_INFO // Fallback
    }
}

// Helper to safely parse time
private fun parseInstant(isoString: String): Instant {
    return try {
        Instant.parse(isoString)
    } catch (e: Exception) {
        Instant.fromEpochMilliseconds(0) // Fallback to epoch if parsing fails
    }
}

fun PatientResponseDto.toEntity(): Patient {
    return Patient(
        id = this.userId?.toString() ?: "",
        fullName = this.name ?: "Unknown",
        gender = Gender.Male, // Backend does not return gender in PatientResponseDto
        age = 0, // Backend does not return birthDate in PatientResponseDto
        birthDate = null,
        weight = this.weight ?: 0.0,
        height = this.height ?: 0.0,
        bloodType = this.bloodType ?: BloodType.UNKNOWN,
        maritalStatus = this.maritalStatus ?: MaritalStatus.Single,
        allergies = this.allergies.map { it.toEntity() },
        chronicDiseases = this.chronicDiseases.map { it.toEntity() },
        familyHistories = this.familyHistories.map { it.toEntity() },
        surgeries = this.surgeries.map { it.toEntity() },
        emergencyContacts = this.emergencyContacts.map { it.toEntity() }
    )
}

fun AllergyResponseDto.toEntity() = AllergyEntity(
    id = id?.toString() ?: "-1",
    name = name ?: "Unknown Allergy",
    symptoms = description ?: "No symptoms",
    dateAdded ="N/A" // Or some default value if not provided by backend
)

// Helper to convert backend ISO date (YYYY-MM-DD...) to UI string (DDMMYYYY)
private fun String?.toUiDateString(): String {
    if (this == null) return ""
    return try {
        val datePart = this.take(10)
        val localDate = LocalDate.parse(datePart)
        val dd = localDate.dayOfMonth.toString().padStart(2, '0')
        val mm = localDate.monthNumber.toString().padStart(2, '0')
        val yyyy = localDate.year.toString().padStart(4, '0')
        "$dd$mm$yyyy"
    } catch (e: Exception) {
        this.filter { it.isDigit() }.take(8)
    }
}

// Helper to convert UI string (DDMMYYYY) to backend ISO date (YYYY-MM-DD)
private fun String.toBackendDateString(): String {
    val digits = this.filter { it.isDigit() }
    if (digits.length == 8) {
        val dd = digits.substring(0, 2)
        val mm = digits.substring(2, 4)
        val yyyy = digits.substring(4, 8)
        return "$yyyy-$mm-$dd"
    }
    return this
}

private fun String?.toBackendDateStringOrNull(): String? {
    if (this.isNullOrBlank()) return null
    return this.toBackendDateString()
}

fun ChronicDiseaseResponseDto.toEntity() = ChronicDiseaseEntity(
    id = id?.toString() ?: "-1",
    name = name ?: "Unknown Disease",
    description = description,
    diagnosisDate = diagnosisDate?.toUiDateString()
)

fun FamilyHistoryResponseDto.toEntity() = FamilyHistoryEntity(
    id = id?.toString() ?: "-1",
    relation = relation ?: FamilyRelation.Unknown,
    condition = condition ?: "Unknown",
    notes = notes
)

fun SurgeryResponseDto.toEntity() = SurgeryEntity(
    id = id?.toString() ?: "-1",
    name = name ?: "Unknown Surgery",
    description = description,
    date = date?.toUiDateString() ?: ""
)

fun EmergencyContactResponseDto.toEntity() = EmergencyContactEntity(
    id = id?.toString() ?: "-1",
    name = name ?: "Unknown",
    relation = relation ?: "Unknown",
    phoneNumber = phoneNumber ?: "",
    email = email ?: "",
    address = address ?: "",
    notes = notes
)

// --- Domain to DTO Mappers ---

fun CreatePatientParams.toDto() = CreatePatientRequestDto(
    birthDate = birthDate,
    height = height,
    weight = weight,
    gender = gender,
    bloodType = bloodType,
    maritalStatus = maritalStatus
)

fun UpdatePatientParams.toDto() = UpdatePatientRequestDto(
    height = height,
    weight = weight,
    bloodType = bloodType,
    maritalStatus = maritalStatus
)

fun AllergyParams.toDto() = CreateAllergyRequestDto(
    name = name,
    description = symptoms
)

fun AllergyParams.toUpdateDto() = UpdateAllergyRequestDto(
    name = name,
    description = symptoms
)

fun ChronicDiseaseParams.toDto() = CreateChronicDiseaseRequestDto(
    name = name,
    description = description,
    diagnosisDate = diagnosisDate?.toBackendDateStringOrNull()
)

fun ChronicDiseaseParams.toUpdateDto() = UpdateChronicDiseaseRequestDto(
    name = name,
    description = description,
    diagnosisDate = diagnosisDate?.toBackendDateStringOrNull()
)

fun FamilyHistoryParams.toDto() = CreateFamilyHistoryRequestDto(
    relation = relation,
    condition = condition,
    notes = notes
)

fun FamilyHistoryParams.toUpdateDto() = UpdateFamilyHistoryRequestDto(
    relation = relation,
    condition = condition,
    notes = notes
)

fun SurgeryParams.toDto() = CreateSurgeryRequestDto(
    name = name,
    description = description,
    date = date.toBackendDateString()
)

fun SurgeryParams.toUpdateDto() = UpdateSurgeryRequestDto(
    name = name,
    description = description,
    date = date.toBackendDateString()
)

fun EmergencyContactParams.toDto() = CreateEmergencyContactRequestDto(
    name = name,
    relation = relation,
    phoneNumber = phoneNumber,
    email = email,
    address = address,
    notes = notes
)

fun EmergencyContactParams.toUpdateDto() = UpdateEmergencyContactRequestDto(
    name = name,
    relation = relation,
    phoneNumber = phoneNumber,
    email = email,
    address = address,
    notes = notes
)

fun ChatConversationDto.toDomain(): ChatConversation {
    return ChatConversation(
        doctorId = this.doctorId,
        doctorName = this.doctorName,
        doctorImageUrl = this.doctorImage,
        lastMessage = this.lastMessage,
        lastMessageTime = this.lastMessageTime, // Add Date Parsing logic here if needed
        unreadCount = this.unreadCount,
        isOnline = this.isOnline
    )
}

fun MessageDto.toDomain(currentUserId: String): Message {
    return Message(
        id = this.id,
        text = this.text,
        senderId = this.senderId,
        timestamp = try { Instant.parse(this.timestamp) } catch (e: Exception) { Instant.fromEpochMilliseconds(0) },
        status = mapMessageStatus(this.status),
        isFromUser = this.senderId == currentUserId
    )
}

private fun mapMessageStatus(status: String): MessageStatus {
    return when (status.lowercase()) {
        "sending" -> MessageStatus.SENDING
        "sent" -> MessageStatus.SENT
        "delivered" -> MessageStatus.DELIVERED
        "read" -> MessageStatus.READ
        "failed" -> MessageStatus.FAILED
        else -> MessageStatus.SENT
    }
}

fun ManagedUserDto.toDomain():ManagedUser {
    return ManagedUser(
        id = this.id,
        name = this.name,
        role = this.role ?: "unknown",
        status = this.status ?: "pending"
    )
}

fun DiagnosisResponseDto.toDomain() = Diagnosis(
    id = id,
    patientUserId = patientUserId,
    doctorUserId = doctorUserId,
    appointmentId = appointmentId,
    symptoms = symptoms,
    summary = summary,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun CreateDiagnosisParams.toDto() = CreateDiagnosisRequestDto(
    appointmentId = appointmentId,
    symptoms = symptoms,
    summary = summary
)

fun UpdateDiagnosisParams.toDto() = UpdateDiagnosisRequestDto(
    symptoms = symptoms,
    summary = summary
)
