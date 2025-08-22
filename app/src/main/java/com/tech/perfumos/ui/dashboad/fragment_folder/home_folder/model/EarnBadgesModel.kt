package com.tech.perfumos.ui.dashboad.fragment_folder.home_folder.model


import com.google.gson.annotations.SerializedName
import com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder.BadgeId


data class EarnBadgesModel(
    @SerializedName("data")
    var `data`: ArrayList<BadgeId?>?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("success")
    var success: Boolean?
)