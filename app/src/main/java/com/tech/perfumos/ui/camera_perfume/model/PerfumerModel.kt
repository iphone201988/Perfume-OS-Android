package com.tech.perfumos.ui.camera_perfume.model


import com.google.gson.annotations.SerializedName


data class PerfumerModel(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("success")
    var success: Boolean?
) {

    data class Data(
        @SerializedName("perfumer")
        var perfumer: Perfumer?,
        @SerializedName("perfumes")
        var perfumes: ArrayList<SimilarPerfume?>?,
        @SerializedName("totalCount")
        var totalCount: Int?
    ) {

        data class Perfumer(
            @SerializedName("bigImage")
            var bigImage: String?,
            @SerializedName("createdAt")
            var createdAt: String?,
            @SerializedName("description")
            var description: String?,
            @SerializedName("_id")
            var id: String?,
            @SerializedName("name")
            var name: String?,
            @SerializedName("smallImage")
            var smallImage: String?,
            @SerializedName("updatedAt")
            var updatedAt: String?,
            @SerializedName("url")
            var url: String?,
            @SerializedName("__v")
            var v: Int?,
            @SerializedName("isFavorite")
            var isFavorite: Boolean?
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