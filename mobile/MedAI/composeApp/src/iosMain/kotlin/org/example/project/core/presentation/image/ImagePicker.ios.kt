package org.example.project.core.presentation.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberImagePicker(onImagePicked: (ByteArray) -> Unit): ImagePickerLauncher {
    return remember {
        object : ImagePickerLauncher {
            override fun launch() {
                // Simulate an image selection on iOS for prototyping/mocking
                onImagePicked(ByteArray(128))
            }
        }
    }
}
