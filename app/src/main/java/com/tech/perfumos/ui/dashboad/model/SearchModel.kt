package com.tech.perfumos.ui.dashboad.model


import com.google.gson.annotations.SerializedName


data class SearchModel(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("success")
    var success: Boolean?
) {
    data class Data(
        @SerializedName("pagination")
        var pagination: Pagination?,
        @SerializedName("perfumes")
        var perfumes: ArrayList<HistoryPerfumeList?>?
    ) {
        data class Pagination(
            @SerializedName("currentPage")
            var currentPage: Int?,
            @SerializedName("perPage")
            var perPage: Int?,
            @SerializedName("totalCount")
            var totalCount: Int?
        )
        /*data class Perfume(
            @SerializedName("brand")
            var brand: String?,
            @SerializedName("_id")
            var id: String?,
            @SerializedName("image")
            var image: String?,
            @SerializedName("name")
            var name: String?
        )*/
    }
}