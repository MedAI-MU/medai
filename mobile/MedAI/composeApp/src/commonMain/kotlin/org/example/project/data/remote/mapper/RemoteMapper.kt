package org.example.project.data.remote.mapper

import kotlinx.datetime.Instant
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
import org.example.project.data.remote.dto.DoctorDto
import org.example.project.data.remote.dto.NotificationDto
import org.example.project.data.remote.dto.UserDto
import org.example.project.design_system.icons.MedAIIcons
import org.example.project.domain.model.AppointmentStatus
import org.example.project.domain.model.Category
import org.example.project.domain.model.CategoryType
import org.example.project.domain.model.Doctor
import org.example.project.domain.model.Notification
import org.example.project.domain.model.NotificationType
import org.example.project.domain.model.User

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
        role = this.role ?: "patient",
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
