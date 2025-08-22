package com.tech.perfumos.ui.dashboad.fragment_folder.articles_folder

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ArticleModel(
     @SerializedName("data")
    var `data`: Data?,
     @SerializedName("message")
    var message: String?,
     @SerializedName("success")
    var success: Boolean?
) {
    
    data class Data(
         @SerializedName("articles")
        var articles: ArrayList<Article?>?,
         @SerializedName("pagination")
        var pagination: Pagination?
    ) {

        data class Pagination(
             @SerializedName("currentPage")
            var currentPage: Int?,
             @SerializedName("perPage")
            var perPage: Int?,
            @SerializedName("totalCount")
            var totalCount: Int?
        )
    }
}

data class Article(
    @SerializedName("content")
    var content: String?,
    @SerializedName("createdAt")
    var createdAt: String?,
    @SerializedName("_id")
    var id: String?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("__v")
    var v: Int?
) :Serializable
