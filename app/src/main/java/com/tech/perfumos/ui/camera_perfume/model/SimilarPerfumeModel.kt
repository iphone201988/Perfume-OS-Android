package com.tech.perfumos.ui.camera_perfume.model


import com.google.gson.annotations.SerializedName


data class SimilarPerfumeModel(
    @SerializedName("data")
    var `data`: ArrayList<SimilarPerfume?>?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("success")
    var success: Boolean?
) {

}