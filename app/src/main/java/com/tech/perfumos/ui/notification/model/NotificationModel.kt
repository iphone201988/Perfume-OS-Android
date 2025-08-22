package com.tech.perfumos.ui.notification.model

import com.google.gson.annotations.SerializedName


data class NotificationModel(
    @SerializedName("data")
    var `data`: ArrayList<Data?>?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("pagination")
    var pagination: Pagination?,
    @SerializedName("success")
    var success: Boolean?,
    @SerializedName("unreadNotifications")
    var unreadNotifications: Int?
) {

    data class Data(
        @SerializedName("createdAt")
        var createdAt: String?,
        @SerializedName("follow")
        var follow: Follow?,
        @SerializedName("followId")
        var followId: String?,
        @SerializedName("_id")
        var id: String?,
        @SerializedName("isRead")
        var isRead: Boolean?,
        @SerializedName("message")
        var message: String?,
        @SerializedName("quiz")
        var quiz: Quiz?,
        @SerializedName("quizId")
        var quizId: String?,
        @SerializedName("title")
        var title: String?,
        @SerializedName("type")
        var type: String?,
        @SerializedName("updatedAt")
        var updatedAt: String?,
        @SerializedName("userId")
        var userId: String?,
        @SerializedName("__v")
        var v: Int?
    ) {





    }


    data class Pagination(
        @SerializedName("currentPage")
        var currentPage: Int?,
        @SerializedName("perPage")
        var perPage: Int?,
        @SerializedName("totalCount")
        var totalCount: Int?,
        @SerializedName("totalPage")
        var totalPage: Int?
    )
}

data class Quiz(
    @SerializedName("createdAt")
    var createdAt: String?,
    @SerializedName("hostId")
    var hostId: String?,
    @SerializedName("_id")
    var id: String?,
    @SerializedName("mode")
    var mode: String?,
    @SerializedName("playType")
    var playType: String?,
    @SerializedName("players")
    var players: ArrayList<Player?>?,
    @SerializedName("questions")
    var questions: ArrayList<String?>?,
    @SerializedName("quizCategory")
    var quizCategory: String?,
    @SerializedName("quizType")
    var quizType: String?,
    @SerializedName("roomId")
    var roomId: Int?,
    @SerializedName("status")
    var status: String?,
    @SerializedName("totalQuestions")
    var totalQuestions: Int?,
    @SerializedName("updatedAt")
    var updatedAt: String?,
    @SerializedName("__v")
    var v: Int?
) {

    data class Player(
        @SerializedName("answers")
        var answers: List<Any?>?,
        @SerializedName("correctAnswers")
        var correctAnswers: Int?,
        @SerializedName("_id")
        var id: String?,
        @SerializedName("isActive")
        var isActive: Boolean?,
        @SerializedName("joinedAt")
        var joinedAt: String?,
        @SerializedName("pointsEarned")
        var pointsEarned: Int?,
        @SerializedName("score")
        var score: Int?,
        @SerializedName("userId")
        var userId: String?
    )
}

data class Follow(
    @SerializedName("createdAt")
    var createdAt: String?,
    @SerializedName("followId")
    var followId: String?,
    @SerializedName("followUser")
    var followUser: FollowUser?,
    @SerializedName("_id")
    var id: String?,
    @SerializedName("userId")
    var userId: String?,
    @SerializedName("__v")
    var v: Int?
) {

    data class FollowUser(
        @SerializedName("fullname")
        var fullname: String?,
        @SerializedName("_id")
        var id: String?,
        @SerializedName("profileImage")
        var profileImage: String?
    )
}