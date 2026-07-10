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

@Composable
actual fun rememberFilePicker(
    allowedTypes: List<String>,
    onFilePicked: (ByteArray, String) -> Unit
): FilePickerLauncher {
    return remember {
        object : FilePickerLauncher {
            override fun launch() {
                val isPdf = allowedTypes.contains("application/pdf")
                val fileName = if (isPdf) "mock_report.pdf" else "mock_scan.png"
                onFilePicked(ByteArray(1024), fileName)
            }
        }
    }
}
