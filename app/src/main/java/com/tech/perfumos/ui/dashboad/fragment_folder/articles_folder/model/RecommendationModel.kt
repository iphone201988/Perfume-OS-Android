package com.tech.perfumos.ui.dashboad.fragment_folder.articles_folder.model

import com.google.gson.annotations.SerializedName

data class RecommendationModel(
    @SerializedName("data")
    var `data`: ArrayList<RecommendationPerfumeList?>?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("pagination")
    var pagination: Pagination?,
    @SerializedName("success")
    var success: Boolean?
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
data class RecommendationPerfumeList(
    @SerializedName("brand")
    var brand: String?,
    @SerializedName("_id")
    var id: String?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("matchScore")
    var matchScore: Int?,
    @SerializedName("name")
    var name: String?
)




