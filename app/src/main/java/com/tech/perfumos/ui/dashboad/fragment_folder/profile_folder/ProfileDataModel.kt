package com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder


import com.google.gson.annotations.SerializedName
import com.tech.perfumos.ui.auth_folder.model.SocialLinkedAccount

data class ProfileDataModel(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("success")
    var success: Boolean?
) {
    data class Data(
        @SerializedName("averageRating")
        var averageRating: Double?,
        @SerializedName("badges")
        var badges: ArrayList<BadgeModel?>?,
        @SerializedName("collections")
        var collections: ArrayList<UserPerfumeList?>?,
        @SerializedName("dob")
        var dob: String?,
        @SerializedName("email")
        var email: String?,
        @SerializedName("enjoySmell")
        var enjoySmell: List<String?>?,
        @SerializedName("followers")
        var followers: Int?,
        @SerializedName("following")
        var following: Int?,
        @SerializedName("fullname")
        var fullname: String?,
        @SerializedName("gender")
        var gender: String?,
        @SerializedName("_id")
        var id: String?,
        @SerializedName("isBlocked")
        var isBlocked: Boolean?,
        @SerializedName("isDeleted")
        var isDeleted: Boolean?,
        @SerializedName("isFollowing")
        var isFollowing: Boolean?,
        @SerializedName("isNotificationOn")
        var isNotificationOn: Boolean?,
        @SerializedName("isVerified")
        var isVerified: Boolean?,
        @SerializedName("language")
        var language: Any?,
        @SerializedName("perfumeBudget")
        var perfumeBudget: String?,
        @SerializedName("perfumeStrength")
        var perfumeStrength: Int?,
        @SerializedName("profileImage")
        var profileImage: String?,
        @SerializedName("rankPoints")
        var rankPoints: Int?,
        @SerializedName("reasonForWearPerfume")
        var reasonForWearPerfume: String?,
        @SerializedName("referralCode")
        var referralCode: String?,
        @SerializedName("referralSource")
        var referralSource: String?,
        @SerializedName("reviews")
        var reviews: ArrayList<UserReview?>?,
        @SerializedName("socialLinkedAccounts")
        var socialLinkedAccounts: ArrayList<SocialLinkedAccount?>?,
        @SerializedName("step")
        var step: Int?,
        @SerializedName("theme")
        var theme: String?,
        @SerializedName("timezone")
        var timezone: Any?,
        @SerializedName("totalBadges")
        var totalBadges: Int?,
        @SerializedName("totalCollection")
        var totalCollection: Int?,
        @SerializedName("totalReviews")
        var totalReviews: Int?,
        @SerializedName("totalWishlist")
        var totalWishlist: Int?,
        @SerializedName("tutorialProgess")
        var tutorialProgess: Int?,
        @SerializedName("username")
        var username: String?,
        @SerializedName("wishlists")
        var wishlists: ArrayList<UserPerfumeList?>?,
        @SerializedName("favorites")
        var favorites: ArrayList<FavoriteList?>?,
        @SerializedName("totalFavorites")
        var totalFavorites: Int?
    ) {


    }
}

data class UserPerfumeList(
    @SerializedName("createdAt")
    var createdAt: String?,
    @SerializedName("_id")
    var id: String?,
    @SerializedName("perfumeId")
    var perfumeId: PerfumeId?,
    @SerializedName("userId")
    var userId: String?,
    @SerializedName("__v")
    var v: Int?
) {
    data class PerfumeId(
        @SerializedName("brand")
        var brand: String?,
        @SerializedName("_id")
        var id: String?,
        @SerializedName("image")
        var image: String?,
        @SerializedName("name")
        var name: String?
    )
}


data class FavoriteList(
    @SerializedName("articleId")
    var articleId: ArticleId?,
    @SerializedName("createdAt")
    var createdAt: String?,
    @SerializedName("_id")
    var id: String?,
    @SerializedName("noteId")
    var noteId: NoteId?,
    @SerializedName("perfumeId")
    var perfumeId: PerfumeId?,
    @SerializedName("perfumerId")
    var perfumerId: PerfumerId?,
    @SerializedName("type")
    var type: String?,
    @SerializedName("userId")
    var userId: String?,
    @SerializedName("__v")
    var v: Int?
) {

    data class NoteId(
        @SerializedName("bgUrl")
        var bgUrl: String?,
        @SerializedName("_id")
        var id: String?,
        @SerializedName("name")
        var name: String?
    )


    data class PerfumeId(
        @SerializedName("brand")
        var brand: String?,
        @SerializedName("_id")
        var id: String?,
        @SerializedName("image")
        var image: String?,
        @SerializedName("name")
        var name: String?
    )

    data class PerfumerId(
        @SerializedName("_id")
        var id: String?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("smallImage")
        var smallImage: String?
    )

    data class ArticleId(
        @SerializedName("_id")
        var id: String?,
        @SerializedName("title")
        var title: String?,
        @SerializedName("image")
        var image: String?
    )

}


data class UserWishlist(
    @SerializedName("createdAt")
    var createdAt: String?,
    @SerializedName("_id")
    var id: String?,
    @SerializedName("perfumeId")
    var perfumeId: PerfumeId?,
    @SerializedName("userId")
    var userId: String?,
    @SerializedName("__v")
    var v: Int?
) {
    data class PerfumeId(
        @SerializedName("brand")
        var brand: String?,
        @SerializedName("_id")
        var id: String?,
        @SerializedName("image")
        var image: String?,
        @SerializedName("name")
        var name: String?
    )
}

data class UserReview(
    @SerializedName("createdAt")
    var createdAt: String?,
    @SerializedName("_id")
    var id: String?,
    @SerializedName("perfumeId")
    var perfumeId: PerfumeId?,
    @SerializedName("rating")
    var rating: Double?,
    @SerializedName("review")
    var review: String?,
    @SerializedName("userId")
    var userId: UserId?,
    @SerializedName("title")
    var title: String?
) {

    data class PerfumeId(
        @SerializedName("brand")
        var brand: String?,
        @SerializedName("_id")
        var id: String?,
        @SerializedName("image")
        var image: String?,
        @SerializedName("name")
        var name: String?
    )


    data class UserId(
        @SerializedName("fullname")
        var fullname: String?,
        @SerializedName("_id")
        var id: String?,
        @SerializedName("profileImage")
        var profileImage: String?
    )
}

data class BadgeModel(
    @SerializedName("badgeId")
    var badgeId: BadgeId?,
    @SerializedName("createdAt")
    var createdAt: String?,
    @SerializedName("_id")
    var id: String?,
    @SerializedName("userId")
    var userId: String?,
    @SerializedName("__v")
    var v: Int?
) {

}
data class BadgeId(
    @SerializedName("createdAt")
    var createdAt: String?,
    @SerializedName("_id")
    var id: String?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("__v")
    var v: Int?
)