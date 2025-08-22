package com.tech.perfumos.ui.quiz.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class SubmitQuizRequest(
    @SerializedName("quizId")
    val quizId: String,
    @SerializedName("answers")
    val answers: SubmitAnswers
)

data class SubmitAnswers(
    @SerializedName("questionId")
    val questionId: String,
    @SerializedName("selectedAnswer")
    val selectedAnswer: String
)

//
//data class SubmitQuizModel(
//    @SerializedName("answeredQuestion")
//    var answeredQuestion: AnsweredQuestion?,
//    @SerializedName("currentScore")
//    var currentScore: Int?,
//    @SerializedName("isCorrect")
//    var isCorrect: Boolean?,
//    @SerializedName("message")
//    var message: String?,
//    @SerializedName("status")
//    var status: String?,
//    @SerializedName("passed")
//    var passed: Boolean?,
//    @SerializedName("pointsEarned")
//    var pointsEarned: Int?,
//    @SerializedName("success")
//    var success: Boolean?,
//    @SerializedName("totalAnswered")
//    var totalAnswered: Int?,
//    @SerializedName("totalQuestions")
//    var totalQuestions: Int?
//) : Serializable {
//    data class AnsweredQuestion(
//        @SerializedName("correctAnswer")
//        var correctAnswer: String?,
//        @SerializedName("isCorrect")
//        var isCorrect: Boolean?,
//        @SerializedName("questionId")
//        var questionId: String?,
//        @SerializedName("selectedAnswer")
//        var selectedAnswer: String?
//    ): Serializable
//}
data class SubmitQuizModel(
    @SerializedName("answeredQuestion")
    val answeredQuestion: AnsweredQuestion,
    @SerializedName("currentScore")
    val currentScore: Int,
    @SerializedName("isCorrect")
    val isCorrect: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("pointsEarned")
    val pointsEarned: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("totalAnswered")
    val totalAnswered: Int,
    @SerializedName("totalQuestions")
    val totalQuestions: Int
):Serializable

data class AnsweredQuestion(
    @SerializedName("correctAnswer")
    val correctAnswer: String,
    @SerializedName("isCorrect")
    val isCorrect: Boolean,
    @SerializedName("questionId")
    val questionId: String,
    @SerializedName("selectedAnswer")
    val selectedAnswer: String
):Serializable