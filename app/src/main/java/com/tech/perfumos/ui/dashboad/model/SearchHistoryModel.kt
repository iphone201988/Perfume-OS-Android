package com.tech.perfumos.ui.dashboad.model


import com.google.gson.annotations.SerializedName


data class SearchHistoryModel(
    @SerializedName("data")
    var `data`: SearchHistoryData?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("success")
    var success: Boolean?
) {

    data class SearchHistoryData(
        @SerializedName("recentPerfumes")
        var recentPerfumes: ArrayList<HistoryPerfumeList?>?,
        @SerializedName("topPerfumes")
        var topPerfumes: ArrayList<HistoryPerfumeList?>?
    ) {

        /*data class RecentPerfume(
            @SerializedName("brand")
            var brand: String?,
            @SerializedName("_id")
            var id: String?,
            @SerializedName("image")
            var image: String?,
            @SerializedName("name")
            var name: String?
        )
        data class TopPerfume(
            @SerializedName("brand")
            var brand: String?,
            @SerializedName("count")
            var count: Int?,
            @SerializedName("_id")
            var id: String?,
            @SerializedName("image")
            var image: String?,
            @SerializedName("name")
            var name: String?
        )*/
    }
}

data class HistoryPerfumeList(
    @SerializedName("brand")
    var brand: String?,
    @SerializedName("_id")
    var id: String?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("name")
    var name: String?
)