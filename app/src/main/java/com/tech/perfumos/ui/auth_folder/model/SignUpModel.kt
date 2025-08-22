package com.tech.perfumos.ui.auth_folder.model


import com.google.gson.annotations.SerializedName


data class SignUpModel(
    @SerializedName("data")
    var `data`: UserDataModel?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("success")
    var success: Boolean?
) {

    /*data class Data(
        @SerializedName("dob")
        var dob: Any?,
        @SerializedName("email")
        var email: String?,
        @SerializedName("enjoySmell")
        var enjoySmell: List<Any?>?,
        @SerializedName("fullname")
        var fullName: String?,
        @SerializedName("gender")
        var gender: Any?,
        @SerializedName("_id")
        var id: String?,
        @SerializedName("isBlocked")
        var isBlocked: Boolean?,
        @SerializedName("isDeleted")
        var isDeleted: Boolean?,
        @SerializedName("isNotificationOn")
        var isNotificationOn: Boolean?,
        @SerializedName("isVerified")
        var isVerified: Boolean?,
        @SerializedName("language")
        var language: Any?,
        @SerializedName("perfumeBudget")
        var perfumeBudget: Any?,
        @SerializedName("perfumeStrength")
        var perfumeStrength: Int?,
        @SerializedName("profileImage")
        var profileImage: Any?,
        @SerializedName("reasonForWearPerfume")
        var reasonForWearPerfume: Any?,
        @SerializedName("referralCode")
        var referralCode: String?,
        @SerializedName("referralSource")
        var referralSource: Any?,
        @SerializedName("socialLinkedAccounts")
        var socialLinkedAccounts: List<Any?>?,
        @SerializedName("step")
        var step: Int?,
        @SerializedName("timezone")
        var timezone: Any?,
        @SerializedName("token")
        var token: String?,
        @SerializedName("username")
        var username: String?
    )*/
}