package org.example.project.domain.model.specialty

import org.example.project.core.presentation.util.UiText

data class Specialty(
    val id: String,
    val title: UiText,
    val iconName: String,
    val doctorCount: Int = 0
)
