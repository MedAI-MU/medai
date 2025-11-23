package org.example.project.core.data

import android.content.Context
import org.example.project.AndroidAppHelper

actual class OnboardingStorage actual constructor() {
    private val context = AndroidAppHelper.getContext()

    private val sharedPreferences = context.getSharedPreferences("MedAI_Prefs", Context.MODE_PRIVATE)

    actual fun isOnboardingCompleted(): Boolean {
        return sharedPreferences.getBoolean("onboarding_completed", false)
    }

    actual fun setOnboardingCompleted(completed: Boolean) {
        sharedPreferences.edit().putBoolean("onboarding_completed", completed).apply()
    }
}
