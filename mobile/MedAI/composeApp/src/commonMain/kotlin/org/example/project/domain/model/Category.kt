package org.example.project.domain.model

import org.jetbrains.compose.resources.StringResource

data class Category(
    val id: String,
    val title: StringResource,
    // In real app, use DrawableResource. Using Int/Vector for mock simplicity
    val iconName: String
)
