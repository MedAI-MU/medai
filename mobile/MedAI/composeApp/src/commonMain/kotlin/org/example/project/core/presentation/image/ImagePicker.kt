package org.example.project.core.presentation.image

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePicker(onImagePicked: (ByteArray) -> Unit): ImagePickerLauncher

interface ImagePickerLauncher {
    fun launch()
}

@Composable
expect fun rememberFilePicker(
    allowedTypes: List<String>,
    onFilePicked: (ByteArray, String) -> Unit
): FilePickerLauncher

interface FilePickerLauncher {
    fun launch()
}
