package org.example.project.core.data

import platform.Foundation.NSUserDefaults

actual class OnboardingStorage actual constructor() {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    actual fun isOnboardingCompleted(): Boolean {
        return userDefaults.boolForKey("onboarding_completed")
    }

    actual fun setOnboardingCompleted(completed: Boolean) {
        userDefaults.setBool(completed, "onboarding_completed")
    }
}
