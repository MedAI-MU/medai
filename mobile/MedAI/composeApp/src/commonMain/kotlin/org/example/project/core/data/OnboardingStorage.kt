package org.example.project.core.data

expect class OnboardingStorage() {
    fun isOnboardingCompleted(): Boolean
    fun setOnboardingCompleted(completed: Boolean)
}
