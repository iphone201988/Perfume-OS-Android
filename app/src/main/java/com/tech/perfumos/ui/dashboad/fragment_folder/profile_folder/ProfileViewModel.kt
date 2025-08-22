package com.tech.perfumos.ui.dashboad.fragment_folder.profile_folder
// data class ProfileViewModel(
//    var `data`: Data?,
//    var message: String?,
//    var status: String?
//) {
//    data class Data(
//        var `data`: Data?,
//        var message: String?,
//        var success: Boolean?
//    ) {
//        data class Data(
//            var pagination: Pagination?,
//            var reviews: List<Review?>?
//        ) {
//            data class Pagination(
//                var currentPage: Int?,
//                var perPage: Int?,
//                var totalCount: Int?
//            )
//
//            data class Review(
//                var __v: Int?,
//                var _id: String?,
//                var authorName: String?,
//                var createdAt: String?,
//                var datePublished: String?,
//                var perfumeId: PerfumeId?,
//                var rating: Double?,
//                var review: String?,
//                var updatedAt: String?,
//                var userId: UserId?
//            ) {
//                data class PerfumeId(
//                    var _id: String?,
//                    var brand: String?,
//                    var image: String?,
//                    var name: String?
//                )
//
//                data class UserId(
//                    var _id: String?,
//                    var fullname: String?,
//                    var profileImage: String?
//                )
//            }
//        }
//    }
//}



import com.google.gson.annotations.SerializedName
data class ProfileViewModel(
    var `data`: Data?,
    var message: String?,
    var status: String?
) {

        data class Data(
            var pagination: Pagination?,
            var reviews: List<Review?>?
        ) {
            data class Pagination(
                var currentPage: Int?,
                var perPage: Int?,
                var totalCount: Int?
            )

            data class Review(
                var __v: Int?,
                var _id: String?,
                var authorName: String?,
                var createdAt: String?,
                var datePublished: String?,
                var perfumeId: PerfumeId?,
                var rating: Double?,
                var review: String?,
                var updatedAt: String?,
                var userId: UserId?,
                var title: String?
            ) {
                data class PerfumeId(
                    var _id: String?,
                    var brand: String?,
                    var image: String?,
                    var name: String?
                )

                data class UserId(
                    var _id: String?,
                    var fullname: String?,
                    var profileImage: String?
                )
            }
        }

}