package com.tech.perfumos.ui.quiz.model

import com.google.gson.annotations.SerializedName

data class InviteFriendModel(
    @SerializedName("data")
    var `data`: ArrayList<InviteFriendList?>?,
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
        @SerializedName("total")
        var total: Int?
    )
}


data class InviteFriendList(
    @SerializedName("fullname")
    var fullname: String?,
    @SerializedName("_id")
    var id: String?,
    @SerializedName("profileImage")
    var profileImage: String?,

    var isSelected : Boolean = true
)


