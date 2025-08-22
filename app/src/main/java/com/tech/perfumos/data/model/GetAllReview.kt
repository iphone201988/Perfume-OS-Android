package com.tech.perfumos.data.model

import com.tech.perfumos.ui.camera_perfume.model.ReviewModel


data class GetAllReview(
    var data: GetAllReviewData?,
    var message: String?,
    var success: Boolean?
) {
    data class GetAllReviewData(
        var pagination: Pagination?,
        var reviews: List<ReviewModel?>?
    ) {
        data class Pagination(
            var currentPage: Int?,
            var perPage: Int?,
            var totalCount: Int?
        )

    }
}