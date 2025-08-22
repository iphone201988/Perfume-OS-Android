package com.tech.perfumos.data.model

import android.graphics.drawable.Drawable

data class Experience(
    val companyName: String, val startDate: String, val endDate: String
)

data class Education(
    val id: String, val educationName: String

)

data class SimpleDummyModel(
    val id: String, val img: Int, val imageType: Boolean = false
)

data class JobTitle(
    val id: String, val title: String

)
data class FilterStatus(
    val id: String, val title: String

)

data class ReviewData(
    val name : String, val image : String, val location : String , val rating : String , val description : String
)
data class NotificationData(
    val image : String, val notification : String, val date : String
)
data class SettingModel(
    val image : String, var name : String
)





