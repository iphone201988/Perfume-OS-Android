package com.tech.perfumos.data.api

object Constants {

    /*  const val BASE_URL = "http://13.51.224.142:3000/api/v1/"
      const val BASE_URL_IMAGE = "http://13.51.224.142:3000"
      const val BASE_URL_SOCKET = "http://13.51.224.142:3000"*/
    private const val IS_DEVELOPMENT = false
    private const val PORT_NUMBER = "192.168.0.190"
    private const val DEV_BASE_URL = "http://$PORT_NUMBER:3000/api/v1/"
    private const val DEV_BASE_URL_IMAGE = "http://$PORT_NUMBER:3000"
    private const val DEV_BASE_URL_SOCKET = "http://$PORT_NUMBER:3000"

    private const val PROD_BASE_URL = "http://13.51.224.142:3000/api/v1/"
    private const val PROD_BASE_URL_IMAGE = "http://13.51.224.142:3000"
    private const val PROD_BASE_URL_SOCKET = "http://13.51.224.142:3000"

    val BASE_URL: String get() = if (IS_DEVELOPMENT) DEV_BASE_URL else PROD_BASE_URL

    val BASE_URL_IMAGE: String get() = if (IS_DEVELOPMENT) DEV_BASE_URL_IMAGE else PROD_BASE_URL_IMAGE

    val BASE_URL_SOCKET: String get() = if (IS_DEVELOPMENT) DEV_BASE_URL_SOCKET else PROD_BASE_URL_SOCKET

    const val MAP_API_KEY = "AIzaSyD5Jt2e9ocVmXovnsOsdmtdhPRkP8m9IhQ"
    const val OPER_AI_KEY =
        "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"

    /**************** API LIST *****************/

    const val HEADER_API = "X-API-Key:lkcMuYllSgc3jsFi1gg896mtbPxIBzYkEL"
    const val LOGIN = "login"
    const val SIGNUP = "signUp"
    const val COUNTRIES = "countries"
    const val STATE = "states"
    const val CITY = "cities"
    const val ProfileUpdate = "job_seeker_details"
    const val DROP_DOWN = "dropdawn_data"

    const val SIGN_UP_API = "user/register"
    const val LOGIN_API = "user/login"
    const val SOCIAL_LOGIN_API = "user/socialLogin"

    const val FORGOT_PASSWORD_API = "user/forgetPassword"
    const val VERIFY_OTP_API = "user/verifyOtp"
    const val RESET_PASSWORD_API = "user/resetPassword"

    const val DELETE_ACCOUNT_API = "user/deleteAccount"

    const val ARTICLE_API = "articles/me"
    const val UPDATE_DATA_API = "user/updateData"
    const val GET_PROFILE_API = "user/me"
    const val GET_HOME_FRAGMENT = "user/home"
    const val UPDATE_PROFILE_API = "user/profileUpdate"


    const val PERFUME_API = "perfume/"
    const val ADD_COLLECTION_API = "user/collection/"
    const val ADD_WISHLIST_API = "user/wishlist/"
    const val ADD_REVIEW_API = "perfume/review"
    const val ADD_TO_FAVORITE_API = "user/favorite"
    const val RECENT_TOP_PERFUME_API = "perfume/recentAndTopSearches"
    const val SEARCH_PERFUME_API = "perfume/search"
    const val GET_PERFUMER_API = "perfume/perfumer"
    const val GET_NOTE_API = "perfume/note"
    const val GET_PERFUMER_ALL_REVIEWS = "perfume/reviews"
    const val GET_PROFILE_REVIEW_API = "user/userData"

    const val SIMILAR_PERFUME_API = "perfume/simillerPerfume"

    const val PERFUME_RECOMMENDATIONS_API = "perfume/perfumeRecommendations"

    const val RANKS_API = "user/ranks"
    const val BADGES_API = "badges/me"
    const val GET_FOLLOWERS_API = "user/followers"

    const val NOTIFICATION_API = "user/notifications"
    const val QUIZ_CATEGORY_API = "user/quizCategory"
    const val LEADERBOARD_API = "user/leaderboardQuiz"

    const val CREATE_QUIZ_API = "user/createQuiz"
    const val SEND_QUIZ_INVITE_API = "user/sendQuizInvite"
    const val JOIN_QUIZ_API = "user/joinQuiz"

    const val SUBMIT_QUIZ_API = "user/submitQuiz"
    const val CHECK_USER_ELIGIBLE_API = "user/checkUserEligible"


    const val FOLLOW_PERSON = "user/follow"

}