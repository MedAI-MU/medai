package org.example.project.domain.model

import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource

data class Category(
    val id: String,
    val title: StringResource,
    val type: CategoryType,
    // In real app, use DrawableResource. Using Int/Vector for mock simplicity
    val iconName: ImageVector
)

enum class CategoryType(val key: String) {
    FAVORITE("favorite"),
    DOCTORS("doctors"),
    PHARMACY("pharmacy"),
    SPECIALTIES("specialties"),
    RECORDS("record"),
    UNKNOWN("unknown"); // Fallback for future proofing

    companion object {
        fun fromKey(key: String): CategoryType {
            return entries.find { it.key.equals(key, ignoreCase = true) } ?: UNKNOWN
        }
    }
}
