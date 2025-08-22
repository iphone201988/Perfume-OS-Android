package com.tech.perfumos.ui.quiz_scorebroard

data class QuizScorebroardModel(
    val `data`: List<Data>,
    val message: String,
    val pagination: Pagination,
    val success: Boolean
)

data class Data(
    val correctAnswers: Int,
    val joinedAt: String,
    val name: String,
    val pointsEarned: Int,
    val profileImage: String,
    val score: Int,
    val status: String,
    val userId: String
)



data class Pagination(
    val currentPage: Int,
    val perPage: Int,
    val totalCount: Int
)