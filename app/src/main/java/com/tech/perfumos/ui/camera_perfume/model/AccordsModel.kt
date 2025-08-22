package com.tech.perfumos.ui.camera_perfume.model

import androidx.annotation.ColorRes

data class AccordsModel(
    val id :Int = 0,
    val name :String,
    val progressStartColor :String,
    val progressEndColor :String,
    val thumbColor: String?,
    val labelColor :String,
    val progress: Int = 50
)
