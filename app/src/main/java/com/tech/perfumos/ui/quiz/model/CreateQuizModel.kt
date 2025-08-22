package com.tech.perfumos.ui.quiz.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CreateQuizModel(
    @SerializedName("data")
    var `data`: QuizDetails?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("success")
    var success: Boolean?
) : Serializable


data class QuizDetails(
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
    var questions: ArrayList<QuizQuestion?>?,
    @SerializedName("quizCategory")
    var quizCategory: QuizCategory?,
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
): Serializable {

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
        var userId: UserId?
    ) : Serializable{

        data class UserId(
            @SerializedName("fullname")
            var fullname: String?,
            @SerializedName("_id")
            var id: String?,
            @SerializedName("profileImage")
            var profileImage: String?
        ): Serializable
    }


    data class QuizCategory(
        @SerializedName("description")
        var description: String?,
        @SerializedName("_id")
        var id: String?,
        @SerializedName("image")
        var image: String?,
        @SerializedName("isDeleted")
        var isDeleted: Boolean?,
        @SerializedName("subTitle")
        var subTitle: String?,
        @SerializedName("title")
        var title: String?,
        @SerializedName("totalQuestions")
        var totalQuestions: Int?,
        @SerializedName("type")
        var type: String?,
        @SerializedName("__v")
        var v: Int?
    )
        : Serializable
}

data class QuizQuestion(
    @SerializedName("correctAnswer")
    var correctAnswer: String?,
    @SerializedName("createdAt")
    var createdAt: String?,
    @SerializedName("explanation")
    var explanation: Any?,
    @SerializedName("_id")
    var id: String?,
    @SerializedName("image")
    var image: Any?,
    @SerializedName("isDeleted")
    var isDeleted: Boolean?,
    @SerializedName("options")
    var options: List<String?>?,
    @SerializedName("questionText")
    var questionText: String?,
    @SerializedName("type")
    var type: String?,
    @SerializedName("updatedAt")
    var updatedAt: String?,
    @SerializedName("__v")
    var v: Int?
): Serializable

/*
data class CreateQuizModel(
    @SerializedName("data")
    var `data`: QuizDetails?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("success")
    var success: Boolean?
) : Serializable

data class QuizDetails(
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
    var players: ArrayList<Any?>?,
    @SerializedName("questions")
    var questions: ArrayList<QuizQuestion?>?,
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
) : Serializable

data class QuizQuestion(
    @SerializedName("correctAnswer")
    var correctAnswer: String?,
    @SerializedName("createdAt")
    var createdAt: String?,
    @SerializedName("explanation")
    var explanation: Any?,
    @SerializedName("_id")
    var id: String?,
    @SerializedName("image")
    var image: Any?,
    @SerializedName("isDeleted")
    var isDeleted: Boolean?,
    @SerializedName("options")
    var options: ArrayList<String?>?,
    @SerializedName("questionText")
    var questionText: String?,
    @SerializedName("type")
    var type: String?,
    @SerializedName("updatedAt")
    var updatedAt: String?,
    @SerializedName("__v")
    var v: Int?
) : Serializable*/
