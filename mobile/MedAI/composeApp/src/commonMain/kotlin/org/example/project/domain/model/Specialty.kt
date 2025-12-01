package org.example.project.domain.model

import org.jetbrains.compose.resources.StringResource

data class Specialty(
    val id: String,
    val title: StringResource,
    val iconName: String,
    val doctorCount: Int = 0
)
