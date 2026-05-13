package org.example.project.domain.model.doctor

data class Doctor(
    val id: String,
    val name: String,
    val specialty: String,
    val imageUrl: String? = null,
    val rating: Double = 0.0,
    val isOnline: Boolean = false,
    val bio: String = "No biography available.",
    val reviewCount: Int = 0
)
