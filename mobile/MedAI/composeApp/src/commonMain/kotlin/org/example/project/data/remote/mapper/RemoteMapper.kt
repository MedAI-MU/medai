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
import org.example.project.data.remote.dto.AllergyDto
import org.example.project.data.remote.dto.AnalysisDto
import org.example.project.data.remote.dto.CategoryDto
import org.example.project.data.remote.dto.DoctorDto
import org.example.project.data.remote.dto.MedicalHistoryDto
import org.example.project.data.remote.dto.NotificationDto
import org.example.project.data.remote.dto.PatientDto
import org.example.project.data.remote.dto.UserDto
import org.example.project.data.remote.dto.VaccinationDto
import org.example.project.design_system.icons.MedAIIcons
import org.example.project.domain.model.AllergyEntity
import org.example.project.domain.model.AnalysisEntity
import org.example.project.domain.model.AnalysisStatus
import org.example.project.domain.model.AppointmentStatus
import org.example.project.domain.model.Category
import org.example.project.domain.model.CategoryType
import org.example.project.domain.model.Doctor
import org.example.project.domain.model.MedicalHistoryEntity
import org.example.project.domain.model.Notification
import org.example.project.domain.model.Patient
import org.example.project.domain.model.User
import org.example.project.domain.model.VaccinationEntity
import org.example.project.domain.model.VaccinationStatus
import org.example.project.data.remote.dto.ChatConversationDto
import org.example.project.data.remote.dto.ChronicDiseaseDto
import org.example.project.data.remote.dto.EmergencyContactDto
import org.example.project.data.remote.dto.FamilyHistoryDto
import org.example.project.data.remote.dto.MessageDto
import org.example.project.data.remote.dto.SurgeryDto
import org.example.project.domain.model.BloodType
import org.example.project.domain.model.ChatConversation
import org.example.project.domain.model.ChronicDiseaseEntity
import org.example.project.domain.model.EmergencyContactEntity
import org.example.project.domain.model.FamilyHistoryEntity
import org.example.project.domain.model.Gender
import org.example.project.domain.model.MaritalStatus
import org.example.project.domain.model.Message
import org.example.project.domain.model.MessageStatus
import org.example.project.domain.model.NotificationType
import org.example.project.data.remote.dto.UpdateAllergyDto
import org.example.project.data.remote.dto.UpdateChronicDiseaseDto
import org.example.project.data.remote.dto.UpdateEmergencyContactDto
import org.example.project.data.remote.dto.UpdateFamilyHistoryDto
import org.example.project.data.remote.dto.UpdateSurgeryDto
import org.example.project.data.remote.dto.CreatePatientDto
import org.example.project.data.remote.dto.UpdatePatientDto
import org.example.project.domain.model.AllergyParams
import org.example.project.domain.model.ChronicDiseaseParams
import org.example.project.domain.model.CreatePatientParams
import org.example.project.domain.model.EmergencyContactParams
import org.example.project.domain.model.FamilyHistoryParams
import org.example.project.domain.model.SurgeryEntity
import org.example.project.domain.model.SurgeryParams
import org.example.project.domain.model.UpdatePatientParams
import org.example.project.domain.model.UserRole

fun mapStatus(status: String?): AppointmentStatus {
    return when (status?.lowercase()) {
        "pending" -> AppointmentStatus.Pending
        "confirmed" -> AppointmentStatus.Confirmed
        "finished" -> AppointmentStatus.Finished
        "waiting" -> AppointmentStatus.Waiting
        "cancelled" -> AppointmentStatus.Cancelled
        else -> AppointmentStatus.Pending // Default fallback
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

 fun DoctorDto.toDomain() = Doctor(
     id = id,
     name = name,
     specialty = specialty,
     rating = rating,
     imageUrl = imageUrl,
     bio = bio ?: "No bio available...",
     reviewCount = reviewCount ?: 0
 )

fun UserDto.toDomain(): User {
    return User(
        id = this.id,
        name = this.name,
        email = this.email,
        role = this.role?.let {
            try {
                UserRole.valueOf(it.uppercase())
            } catch (e: Exception) {
                UserRole.PATIENT
            }
        } ?: UserRole.PATIENT,
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

fun PatientDto.toEntity(): Patient {
    // Assuming birthDate is in "YYYY-MM-DD" or similar ISO format
    val birthDateParsed = try {
        this.birthDate?.let { LocalDate.parse(it.take(10)) }
    } catch (e: Exception) {
        null
    }

    val calculatedAge = if (birthDateParsed != null) {
        try {
            birthDateParsed.yearsUntil(Clock.System.todayIn(TimeZone.currentSystemDefault()))
        } catch (e: Exception) {
            0
        }
    } else 0

    return Patient(
        id = this.userId?.toString() ?: "",
        fullName = this.user?.name ?: this.name ?: "Unknown",
        gender = this.user?.gender ?: this.gender ?: Gender.Male,
        age = calculatedAge,
        birthDate = this.birthDate,
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

fun AllergyDto.toEntity() = AllergyEntity(
    id = id?.toString() ?: "-1",
    name = name ?: "Unknown Allergy",
    symptoms = description ?: "No symptoms",
    dateAdded ="N/A" // Or some default value if not provided by backend
)

fun ChronicDiseaseDto.toEntity() = ChronicDiseaseEntity(
    id = id?.toString() ?: "-1",
    name = name ?: "Unknown Disease",
    description = description,
    diagnosisDate = diagnosisDate
)

fun FamilyHistoryDto.toEntity() = FamilyHistoryEntity(
    id = id?.toString() ?: "-1",
    relation = relation ?: "Unknown",
    condition = condition ?: "Unknown",
    notes = notes
)

fun SurgeryDto.toEntity() = SurgeryEntity(
    id = id?.toString() ?: "-1",
    name = name ?: "Unknown Surgery",
    description = description,
    date = date ?: ""
)

fun EmergencyContactDto.toEntity() = EmergencyContactEntity(
    id = id?.toString() ?: "-1",
    name = name ?: "Unknown",
    relation = relation ?: "Unknown",
    phoneNumber = phoneNumber ?: "",
    email = email ?: "",
    address = address ?: "",
    notes = notes
)

// --- Domain to DTO Mappers ---

fun CreatePatientParams.toDto() = CreatePatientDto(
    birthDate = birthDate,
    height = height,
    weight = weight,
    gender = gender,
    bloodType = bloodType,
    maritalStatus = maritalStatus
)

fun UpdatePatientParams.toDto() = UpdatePatientDto(
    height = height,
    weight = weight,
    bloodType = bloodType,
    maritalStatus = maritalStatus
)

fun AllergyParams.toDto() = AllergyDto(
    name = name,
    description = symptoms
)

fun AllergyParams.toUpdateDto() = UpdateAllergyDto(
    name = name,
    description = symptoms
)

fun ChronicDiseaseParams.toDto() = ChronicDiseaseDto(
    name = name,
    description = description,
    diagnosisDate = diagnosisDate
)

fun ChronicDiseaseParams.toUpdateDto() = UpdateChronicDiseaseDto(
    name = name,
    description = description,
    diagnosisDate = diagnosisDate
)

fun FamilyHistoryParams.toDto() = FamilyHistoryDto(
    relation = relation,
    condition = condition,
    notes = notes
)

fun FamilyHistoryParams.toUpdateDto() = UpdateFamilyHistoryDto(
    relation = relation,
    condition = condition,
    notes = notes
)

fun SurgeryParams.toDto() = SurgeryDto(
    name = name,
    description = description,
    date = date
)

fun SurgeryParams.toUpdateDto() = UpdateSurgeryDto(
    name = name,
    description = description,
    date = date
)

fun EmergencyContactParams.toDto() = EmergencyContactDto(
    name = name,
    relation = relation,
    phoneNumber = phoneNumber,
    email = email,
    address = address,
    notes = notes
)

fun EmergencyContactParams.toUpdateDto() = UpdateEmergencyContactDto(
    name = name,
    relation = relation,
    phoneNumber = phoneNumber,
    email = email,
    address = address,
    notes = notes
)

// --- Analysis Mapper (Restored) ---
fun AnalysisDto.toEntity(): AnalysisEntity {
    return AnalysisEntity(
        id = this.analysis_id,
        type = this.type_name,
        date = try { LocalDate.parse(this.date_performed) } catch (e: Exception) { LocalDate(2000, 1, 1) },
        status = when (this.status_code) {
            1 -> AnalysisStatus.Completed
            2 -> AnalysisStatus.Cancelled
            else -> AnalysisStatus.Pending
        }
    )
}

// --- Vaccination Mapper ---
fun VaccinationDto.toEntity(): VaccinationEntity {
    return VaccinationEntity(
        id = this.vaccine_id,
        name = this.vaccine_name,
        dateAdministered = try { LocalDate.parse(this.admin_date) } catch (e: Exception) { LocalDate(2000, 1, 1) },
        nextDoseDate = this.next_dose?.let { try { LocalDate.parse(it) } catch (e: Exception) { null } },
        status = if (this.is_completed) VaccinationStatus.Done else VaccinationStatus.Scheduled
    )
}

// --- Medical History Mapper ---
fun MedicalHistoryDto.toEntity(): MedicalHistoryEntity {
    return MedicalHistoryEntity(
        id = this.record_id,
        condition = this.condition,
        status = this.current_status,
        treatmentPlan = this.plan,
        attendingDoctor = this.provider_name
    )
}

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
