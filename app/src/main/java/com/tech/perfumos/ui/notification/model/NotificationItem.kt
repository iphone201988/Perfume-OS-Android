package com.tech.perfumos.ui.notification.model

import com.google.gson.annotations.SerializedName
import com.tech.perfumos.ui.notification.model.Follow.FollowUser

sealed class NotificationItem {
    data class QuizInviteItem(
        val id: String,
        val title: String,
        var createdAt: String?,
        val message: String,
        val quizId: String,
        val quiz: Quiz?,
        val isRead: Boolean?,
    ) : NotificationItem()

    data class FollowItem(
        val title: String,
        val message: String,
        var createdAt: String?,
        var followId: String?,
        var followUser: FollowUser?,
        var id: String?,
        var userId: String?,
        var v: Int?,
        val isRead: Boolean?,
    ) : NotificationItem()

    data class AlertItem(
        val id: String,
        val title: String,
        val message: String,
        var createdAt: String?,
        val isRead: Boolean?,
    ) : NotificationItem()
}