package com.tech.perfumos.ui.quiz.model

data class CheckUserEligible(
    val `data`: Data,
    val message: String,
    val success: Boolean
)

data class Data(
    val isRankedQuizUnlocked: Boolean,
    val rankPoints: Int
)