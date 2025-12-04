package org.example.project.core.presentation.util

import org.example.project.core.domain.ResourceProvider
import org.jetbrains.compose.resources.StringResource

class ResourceProviderImpl : ResourceProvider {
    override suspend fun getString(resource: StringResource): String {
        return org.jetbrains.compose.resources.getString(resource)
    }

    override suspend fun getString(resource: StringResource, vararg args: Any): String {
        return org.jetbrains.compose.resources.getString(resource, *args)
    }
}
