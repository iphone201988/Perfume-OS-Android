package com.tech.perfumos.ui.onboarding_folder.model


import com.google.gson.annotations.SerializedName


data class OnBoardingModel(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("success")
    var success: Boolean?
) {

    data class Data(
        @SerializedName("dob")
        var dob: String?,
        @SerializedName("email")
        var email: String?,
        @SerializedName("enjoySmell")
        var enjoySmell: ArrayList<String?>?,
        @SerializedName("fullname")
        var fullname: String?,
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
        @SerializedName("rankPoints")
        var rankPoints: Int?,
        @SerializedName("reasonForWearPerfume")
        var reasonForWearPerfume: Any?,
        @SerializedName("referralCode")
        var referralCode: String?,
        @SerializedName("referralSource")
        var referralSource: Any?,
        @SerializedName("socialLinkedAccounts")
        var socialLinkedAccounts: List<SocialLinkedAccount?>?,
        @SerializedName("step")
        var step: Int?,
        @SerializedName("theme")
        var theme: String?,
        @SerializedName("timezone")
        var timezone: Any?,
        @SerializedName("tutorialProgess")
        var tutorialProgess: Int?,
        @SerializedName("username")
        var username: Any?
    ) {

        data class SocialLinkedAccount(
            @SerializedName("id")
            var id: String?,
            @SerializedName("_id")
            var _id: String?,
            @SerializedName("provider")
            var provider: String?
        )
    }
}