package com.tech.perfumos.ui.onboarding_folder

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes


data class OnboardingModel(
    val category: Int?  = null,
    val imageDrawable: Int,
    val text: String,
    val isImageViewVisible: Boolean,
    val colorText: String,
    @DrawableRes
    val drawable: Int = 0,
    var selected :Boolean?  = false
)

data class GenderList(
    @DrawableRes
    val drawable: Int = 0,
    val text: String,
    var selected :Boolean?  = false
)
