package org.example.project.core.domain

import org.jetbrains.compose.resources.StringResource

interface ResourceProvider {
    suspend fun getString(resource: StringResource): String
    suspend fun getString(resource: StringResource, vararg args: Any): String
}
