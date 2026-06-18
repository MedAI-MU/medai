package org.example.project.core.presentation.image

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    val bitmap = BitmapFactory.decodeByteArray(this, 0, size)
        ?: throw IllegalArgumentException("Failed to decode image bytes into a Bitmap.")
    return bitmap.asImageBitmap()
}
