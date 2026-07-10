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
        val key = iconKey.lowercase().trim()
        return when {
            key.contains("cardio") || key.contains("heart") || key.contains("قلب") -> Icons.Default.Favorite
            key.contains("derm") || key.contains("skin") || key.contains("جلد") -> Icons.Default.Face
            key.contains("gyn") || key.contains("preg") || key.contains("نساء") || key.contains("توليد") || key.contains("ولادة") -> Icons.Default.PregnantWoman
            key.contains("odont") || key.contains("dent") || key.contains("أسنان") || key.contains("اسنان") -> Icons.Default.Face
            key.contains("onco") || key.contains("cancer") || key.contains("أورام") || key.contains("اورام") -> Icons.Default.Science
            key.contains("general") || key.contains("عام") -> Icons.Default.Person
            else -> Icons.Default.MedicalServices
        }
    }
}
