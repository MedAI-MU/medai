package org.example.project.design_system.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PregnantWoman
import androidx.compose.material.icons.filled.Science
import androidx.compose.ui.graphics.vector.ImageVector

object IconMapper {

    fun getCategoryIcon(iconKey: String): ImageVector {
        return when (iconKey.lowercase()) {
            "favorite" -> Icons.Default.FavoriteBorder
            "stethoscope" -> Icons.Default.MedicalServices
            "medication" -> Icons.Default.Medication
            "local_hospital" -> Icons.Default.LocalHospital
            "assignment" -> Icons.Default.Assignment
            else -> Icons.Default.Circle
        }
    }

    fun getSpecialtyIcon(iconKey: String): ImageVector {
        return when (iconKey.lowercase()) {
            "cardiology" -> Icons.Default.Favorite
            "dermatology" -> Icons.Default.Face
            "general" -> Icons.Default.Person
            "gynecology" -> Icons.Default.PregnantWoman
            "odontology" -> Icons.Default.Face
            "oncology" -> Icons.Default.Science
            else -> Icons.Default.Circle
        }
    }
}
