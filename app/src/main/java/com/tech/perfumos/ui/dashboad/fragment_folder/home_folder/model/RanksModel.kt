package com.tech.perfumos.ui.dashboad.fragment_folder.home_folder.model


import com.google.gson.annotations.SerializedName


data class RanksModel(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("success")
    var success: Boolean?
) {

    data class Data(
        @SerializedName("currentRank")
        var currentRank: CurrentRank?,
        @SerializedName("rankPoints")
        var rankPoints: Int?,
        @SerializedName("ranks")
        var ranks: ArrayList<RankList?>?
    ) {

        data class CurrentRank(
            @SerializedName("createdAt")
            var createdAt: String?,
            @SerializedName("description")
            var description: String?,
            @SerializedName("_id")
            var id: String?,
            @SerializedName("image")
            var image: String?,
            @SerializedName("max")
            var max: Int?,
            @SerializedName("min")
            var min: Int?,
            @SerializedName("name")
            var name: String?,
            @SerializedName("__v")
            var v: Int?
        )



    }
}
data class RankList(
    @SerializedName("createdAt")
    var createdAt: String?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("_id")
    var id: String?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("otherImage")
    var otherImage: String?,
    @SerializedName("max")
    var max: Int?,
    @SerializedName("min")
    var min: Int?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("__v")
    var v: Int? ,
    @SerializedName("currentRank")
    var currentRank: Boolean?
)