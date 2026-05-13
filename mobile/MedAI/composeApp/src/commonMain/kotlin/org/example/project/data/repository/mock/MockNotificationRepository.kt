package org.example.project.data.repository.mock


import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import org.example.project.domain.model.notification.Notification
import org.example.project.domain.model.notification.NotificationType
import org.example.project.domain.repository.notification.NotificationRepository
import kotlin.time.Duration.Companion.hours

class MockNotificationRepository : NotificationRepository {
    override suspend fun getNotifications(): Result<List<Notification>> {
        delay(1000)
        val now = Clock.System.now()

        return Result.success(
            listOf(
                Notification(
                    id = "1",
                    title = "Appointment Success!",
                    message = "You have successfully booked your appointment with Dr. Emily Walker.",
                    timestamp = now.minus(1.hours),
                    type = NotificationType.APPOINTMENT_CONFIRMED,
                    isRead = false
                ),
                Notification(
                    id = "2",
                    title = "Schedule Changed",
                    message = "You have successfully changed your appointment with Dr. David Patel.",
                    timestamp = now.minus(5.hours),
                    type = NotificationType.SCHEDULE_CHANGED,
                    isRead = true
                ),
                Notification(
                    id = "3",
                    title = "Appointment Cancelled",
                    message = "You have successfully cancelled your appointment with Dr. David Patel.",
                    timestamp = now.minus(24.hours),
                    type = NotificationType.APPOINTMENT_CANCELLED,
                    isRead = true
                )
            )
        )
    }

    override suspend fun markAsRead(notificationId: String): Result<Unit> {
        delay(300)
        return Result.success(Unit)
    }
}
