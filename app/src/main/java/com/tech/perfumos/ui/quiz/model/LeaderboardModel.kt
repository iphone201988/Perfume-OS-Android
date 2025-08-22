package com.tech.perfumos.ui.quiz.model


import com.google.gson.annotations.SerializedName


data class LeaderboardModel(
    @SerializedName("data")
    var `data`: ArrayList<LeaderBoardList?>?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("pagination")
    var pagination: Pagination?,
    @SerializedName("success")
    var success: Boolean?
) {

    data class Pagination(
        @SerializedName("currentPage")
        var currentPage: Int?,
        @SerializedName("perPage")
        var perPage: Int?,
        @SerializedName("totalCount")
        var totalCount: Int?
    )
}

data class LeaderBoardList(
    @SerializedName("fullname")
    var fullname: String?,
    @SerializedName("profileImage")
    var profileImage: String?,
    @SerializedName("totalCorrectAnswers")
    var totalCorrectAnswers: Int?,
    @SerializedName("totalEarnedPoints")
    var totalEarnedPoints: Int?,
    @SerializedName("userId")
    var userId: String?
)