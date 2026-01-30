package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import org.example.project.data.remote.dto.NotificationDto
import org.example.project.data.remote.mapper.toDomain
import org.example.project.domain.model.Notification
import org.example.project.domain.repository.NotificationRepository


class NetworkNotificationRepository(
    private val client: HttpClient
) : NotificationRepository {

    override suspend fun getNotifications(): Result<List<Notification>> {
        return try {

            val dtos: List<NotificationDto> = client.get("/notifications").body()


            val domainList = dtos.map { it.toDomain() }


            Result.success(domainList)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun markAsRead(notificationId: String): Result<Unit> {
        return try {
            client.post("/notifications/$notificationId/read")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
