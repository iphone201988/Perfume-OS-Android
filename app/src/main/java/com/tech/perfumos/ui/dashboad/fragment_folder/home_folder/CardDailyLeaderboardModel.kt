package com.tech.perfumos.ui.dashboad.fragment_folder.home_folder

import androidx.annotation.ColorRes
import com.tech.perfumos.R


data class CardDailyLeaderboardModel(
    @ColorRes val color: Int = R.color.trophy_color,
    val image:String,
    val name:String,
    @ColorRes
    val textColor :Int = R.color.bottom_bar_color,
    val alpha:Float = 1.0f,
    val isSelected:Boolean = false,
)