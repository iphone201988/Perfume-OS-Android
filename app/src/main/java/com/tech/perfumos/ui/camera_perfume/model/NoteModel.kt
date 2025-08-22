package com.tech.perfumos.ui.camera_perfume.model


import com.google.gson.annotations.SerializedName


data class NoteModel(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("success")
    var success: Boolean?
) {

    data class Data(
        @SerializedName("note")
        var note: Note?,
        @SerializedName("perfumes")
        var perfumes: ArrayList<SimilarPerfume?>?
    ) {

        data class Note(
            @SerializedName("bgUrl")
            var bgUrl: String?,
            @SerializedName("createdAt")
            var createdAt: String?,
            @SerializedName("group")
            var group: String?,
            @SerializedName("_id")
            var id: String?,
            @SerializedName("image")
            var image: String?,
            @SerializedName("isFavorite")
            var isFavorite: Boolean?,
            @SerializedName("name")
            var name: String?,
            @SerializedName("odorProfile")
            var odorProfile: String?,
            @SerializedName("otherNames")
            var otherNames: List<Any?>?,
            @SerializedName("scientificName")
            var scientificName: Any?,
            @SerializedName("thumbnails")
            var thumbnails: List<Thumbnail?>?,
            @SerializedName("updatedAt")
            var updatedAt: String?,
            @SerializedName("url")
            var url: String?,
            @SerializedName("__v")
            var v: Int?
        ) {

            data class Thumbnail(
                @SerializedName("alt")
                var alt: String?,
                @SerializedName("_id")
                var id: String?,
                @SerializedName("src")
                var src: String?
            )
        }




    }
}