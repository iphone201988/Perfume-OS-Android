package com.tech.perfumos.ui.dashboad.fragment_folder.home_folder.model

import com.tech.perfumos.ui.quiz.model.LeaderBoardList

data class HomeFragementModel(
    val leaderboard: List<Leaderboard>,
    val message: String,
    val rank: Rank,
    val success: Boolean
)

data class Leaderboard(
    val _id: String,
    val fullname: String,
    val profileImage: String,
    val rankPoints: Int,
    val username: String
)

data class Rank(
    val rankUpProgress: Int,
    val rankUpScore: Int
)