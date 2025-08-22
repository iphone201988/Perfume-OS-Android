package com.tech.perfumos.ui.camera_perfume.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class PerfumeInfoModel(
    @SerializedName("data")
    var `data`: PerfumeInfoData?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("success")
    var success: Boolean?
) : Serializable
{

    data class PerfumeInfoData(
        @SerializedName("brand")
        var brand: String?,
        @SerializedName("brandImage")
        var brandImage: Any?,
        @SerializedName("createdAt")
        var createdAt: String?,
        @SerializedName("description")
        var description: String?,
        @SerializedName("_id")
        var id: String?,
        @SerializedName("image")
        var image: String?,
        @SerializedName("intendedFor")
        var intendedFor: List<String?>?,
        @SerializedName("isCollection")
        var isCollection: Boolean?,
        @SerializedName("isWishlist")
        var isWishlist: Boolean?,
        @SerializedName("longevity")
        var longevity: Any?,
        @SerializedName("mainAccords")
        var mainAccords: List<MainAccord?>?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("notes")
        var notes: Notes?,
        @SerializedName("occasions")
        var occasions: ArrayList<Occasion?>?,
        @SerializedName("perfumers")
        var perfumers: ArrayList<Perfumer?>?,
        @SerializedName("quotes")
        var quotes: Any?,
        @SerializedName("rating")
        var rating: Rating?,
        @SerializedName("reviews")
        var reviews: ArrayList<ReviewModel?>?,
        @SerializedName("sameBrand")
        var sameBrand: ArrayList<SimilarPerfume?>?,
        @SerializedName("seasons")
        var seasons: ArrayList<Season?>?,
        @SerializedName("sillage")
        var sillage: Any?,
        @SerializedName("similar")
        var similar: ArrayList<SimilarPerfume?>?,
        @SerializedName("totalReviewsAndRatings")
        var totalReviewsAndRatings: TotalReviewsAndRatings?,
        @SerializedName("updatedAt")
        var updatedAt: String?,
        @SerializedName("url")
        var url: String?,
        @SerializedName("__v")
        var v: Int?,
        @SerializedName("year")
        var year: Int?,
        @SerializedName("isFavorite")
        var isFavorite: Boolean?
    ) : Serializable {

        data class MainAccord(
            @SerializedName("backgroundColor")
            var backgroundColor: String?,
            @SerializedName("_id")
            var id: String?,
            @SerializedName("name")
            var name: String?,
            @SerializedName("width")
            var width: String?
        ) : Serializable


        data class Notes(
            @SerializedName("base")
            var base: ArrayList<NotesList?>?,
            @SerializedName("middle")
            var middle: ArrayList<NotesList?>?,
            @SerializedName("notes")
            var notes: ArrayList<NotesList?>?,
            @SerializedName("top")
            var top: ArrayList<NotesList?>?
        ) : Serializable {


            data class Middle(
                @SerializedName("_id")
                var id: String?,
                @SerializedName("image")
                var image: String?,
                @SerializedName("name")
                var name: String?,
                @SerializedName("noteId")
                var noteId: NoteId?
            ) : Serializable {

                data class NoteId(
                    @SerializedName("bgUrl")
                    var bgUrl: String?,
                    @SerializedName("createdAt")
                    var createdAt: String?,
                    @SerializedName("group")
                    var group: String?,
                    @SerializedName("_id")
                    var id: String?,
                    @SerializedName("name")
                    var name: String?,
                    @SerializedName("odorProfile")
                    var odorProfile: String?,
                    @SerializedName("otherNames")
                    var otherNames: List<String?>?,
                    @SerializedName("scientificName")
                    var scientificName: String?,
                    @SerializedName("thumbnails")
                    var thumbnails: List<Thumbnail?>?,
                    @SerializedName("updatedAt")
                    var updatedAt: String?,
                    @SerializedName("url")
                    var url: String?,
                    @SerializedName("__v")
                    var v: Int?
                ) : Serializable {

                    data class Thumbnail(
                        @SerializedName("alt")
                        var alt: String?,
                        @SerializedName("_id")
                        var id: String?,
                        @SerializedName("src")
                        var src: String?
                    ) : Serializable
                }
            }


            data class Top(
                @SerializedName("_id")
                var id: String?,
                @SerializedName("image")
                var image: String?,
                @SerializedName("name")
                var name: String?,
                @SerializedName("noteId")
                var noteId: NoteId?
            ) : Serializable {

                data class NoteId(
                    @SerializedName("bgUrl")
                    var bgUrl: String?,
                    @SerializedName("createdAt")
                    var createdAt: String?,
                    @SerializedName("group")
                    var group: String?,
                    @SerializedName("_id")
                    var id: String?,
                    @SerializedName("name")
                    var name: String?,
                    @SerializedName("odorProfile")
                    var odorProfile: String?,
                    @SerializedName("otherNames")
                    var otherNames: List<String?>?,
                    @SerializedName("scientificName")
                    var scientificName: String?,
                    @SerializedName("thumbnails")
                    var thumbnails: List<Thumbnail?>?,
                    @SerializedName("updatedAt")
                    var updatedAt: String?,
                    @SerializedName("url")
                    var url: String?,
                    @SerializedName("__v")
                    var v: Int?
                ) : Serializable {

                    data class Thumbnail(
                        @SerializedName("alt")
                        var alt: String?,
                        @SerializedName("_id")
                        var id: String?,
                        @SerializedName("src")
                        var src: String?
                    ) : Serializable
                }
            }
        }


        data class Perfumer(
            @SerializedName("_id")
            var id: String?,
            @SerializedName("image")
            var image: String?,
            @SerializedName("name")
            var name: String?,
            @SerializedName("perfumerId")
            var perfumerId: PerfumerId?
        ) : Serializable {

            data class PerfumerId(
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
                var v: Int?
            ) : Serializable
        }
        data class Occasion(
            @SerializedName("_id")
            var id: String?,
            @SerializedName("name")
            var name: String?,
            @SerializedName("width")
            var width: String?
        ) :Serializable

        data class Season(
            @SerializedName("_id")
            var id: String?,
            @SerializedName("name")
            var name: String?,
            @SerializedName("width")
            var width: String?
        ) :Serializable


        data class Rating(
            @SerializedName("score")
            var score: Double?,
            @SerializedName("votes")
            var votes: Int?
        ) : Serializable



        data class SameBrand(
            @SerializedName("brand")
            var brand: String?,
            @SerializedName("_id")
            var id: String?,
            @SerializedName("image")
            var image: String?,
            @SerializedName("name")
            var name: String?
        ) : Serializable





        data class TotalReviewsAndRatings(
            @SerializedName("averageRating")
            var averageRating: Double?,
            @SerializedName("totalReviews")
            var totalReviews: Int?
        ) : Serializable
    }
}

data class NotesList(
    @SerializedName("_id")
    var id: String?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("noteId")
    var noteId: NoteId?
) : Serializable {

    data class NoteId(
        @SerializedName("bgUrl")
        var bgUrl: String?,
        @SerializedName("createdAt")
        var createdAt: String?,
        @SerializedName("group")
        var group: String?,
        @SerializedName("_id")
        var id: String?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("odorProfile")
        var odorProfile: String?,
        @SerializedName("otherNames")
        var otherNames: List<Any?>?,
        @SerializedName("scientificName")
        var scientificName: String?,
        @SerializedName("thumbnails")
        var thumbnails: List<Thumbnail?>?,
        @SerializedName("updatedAt")
        var updatedAt: String?,
        @SerializedName("url")
        var url: String?,
        @SerializedName("__v")
        var v: Int?
    ) : Serializable {

        data class Thumbnail(
            @SerializedName("alt")
            var alt: String?,
            @SerializedName("_id")
            var id: String?,
            @SerializedName("src")
            var src: String?
        ) : Serializable
    }
}

data class SimilarPerfume(
    @SerializedName("brand")
    var brand: String?,
    @SerializedName("_id")
    var id: String?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("name")
    var name: String?
) : Serializable

data class ReviewModel(
    @SerializedName("authorImage")
    var authorImage: String?,
    @SerializedName("authorName")
    var authorName: String?,
    @SerializedName("createdAt")
    var createdAt: String?,
    @SerializedName("datePublished")
    var datePublished: String?,
    @SerializedName("_id")
    var id: String?,
    @SerializedName("perfumeId")
    var perfumeId: String?,
    @SerializedName("rating")
    var rating: Double?,
    @SerializedName("review")
    var review: String?,
    @SerializedName("updatedAt")
    var updatedAt: String?,
    @SerializedName("userId")
    var userId: String?,
    @SerializedName("__v")
    var v: Int?,
    @SerializedName("title")
    var title: String?,

    var isExpand: Boolean = false
): Serializable


/*data class Review(
    @SerializedName("authorImage")
    var authorImage: String?,
    @SerializedName("authorName")
    var authorName: String?,
    @SerializedName("createdAt")
    var createdAt: String?,
    @SerializedName("datePublished")
    var datePublished: String?,
    @SerializedName("_id")
    var id: String?,
    @SerializedName("perfumeId")
    var perfumeId: String?,
    @SerializedName("rating")
    var rating: Int?,
    @SerializedName("review")
    var review: String?,
    @SerializedName("updatedAt")
    var updatedAt: String?,
    @SerializedName("userId")
    var userId: String?,
    @SerializedName("__v")
    var v: Int?
) : Serializable*/

