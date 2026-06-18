package org.example.project.di

import org.example.project.data.local.DATA_STORE_FILE_NAME
import org.example.project.data.local.createDataStore
import org.example.project.domain.ai.AndroidXRayClassifier
import org.example.project.domain.ai.XRayClassifier
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

actual val platformModule= module {
    single {
        createDataStore {
            File(androidContext().filesDir, "datastore/$DATA_STORE_FILE_NAME").absolutePath
        }
    }

    single<XRayClassifier> { AndroidXRayClassifier(androidContext()) }
}
