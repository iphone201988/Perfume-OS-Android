package com.tech.perfumos.ui.quiz.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class QuizCategoryModel(
    @SerializedName("data")
    var `data`: ArrayList<QuizCategoryList?>?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("success")
    var success: Boolean?
)

data class QuizCategoryList(
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
) : Serializable




