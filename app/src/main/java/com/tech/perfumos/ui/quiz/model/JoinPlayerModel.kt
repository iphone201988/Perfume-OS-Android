package com.tech.perfumos.ui.quiz.model

data class JoinPlayerModel(
    val `data`: JoinPlayerData?,
    val roomId: String
)

data class JoinPlayerData(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val hostId: String,
    val mode: String,
    val playType: String,
    val players: List<Player>,
    val questions: List<Question>,
    val quizCategory: QuizCategory,
    val quizType: String,
    val roomId: Int,
    val status: String,
    val totalQuestions: Int,
    val updatedAt: String
)

data class Player(
    val _id: String,
    val answers: List<Any>,
    val correctAnswers: Int,
    val isActive: Boolean,
    val joinedAt: String,
    val pointsEarned: Int,
    val score: Int,
    val userId: UserId
)

data class Question(
    val __v: Int,
    val _id: String,
    val correctAnswer: String,
    val createdAt: String,
    val explanation: Any,
    val image: Any,
    val isDeleted: Boolean,
    val options: List<String>,
    val questionText: String,
    val type: String,
    val updatedAt: String
)

data class QuizCategory(
    val __v: Int,
    val _id: String,
    val description: String,
    val image: String,
    val isDeleted: Boolean,
    val subTitle: String,
    val title: String,
    val totalQuestions: Int,
    val type: String
)

data class UserId(
    val _id: String,
    val fullname: String,
    val profileImage: String
)