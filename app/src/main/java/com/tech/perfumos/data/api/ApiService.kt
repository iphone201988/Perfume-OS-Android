package com.tech.perfumos.data.api

import com.google.gson.JsonObject
import com.tech.perfumos.data.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.QueryMap
import retrofit2.http.Url


interface ApiService {

//    @Header("Authorization") token: String,


    //@Headers(Constants.HEADER_API)
    @POST
    suspend fun apiForRawBody(
        @Body data: HashMap<String, Any>,
        @Url url: String
    ): Response<JsonObject>

    @POST
    suspend fun apiAuthRawBody(
        @Header("Authorization") token: String,
        @Body data: HashMap<String, Any>,
        @Url url: String
    ): Response<JsonObject>

    @POST
    suspend fun apiPostPath(
        @Header("Authorization") token: String,
        @Path("collectionId") collectionId: String,
        @Body data: HashMap<String, Any>,
        @Url url: String
    ): Response<JsonObject>

    /*@Headers(Constants.HEADER_API)*/
    @FormUrlEncoded
    @POST
    suspend fun apiForFormData(
        @FieldMap data: HashMap<String, Any>,
        @Url url: String
    ): Response<JsonObject>

    /*@Headers(Constants.HEADER_API)*/
    @FormUrlEncoded
    @POST
    suspend fun apiAuthFormData(
        @Header("Authorization") token: String,
        @Url url: String,
        @FieldMap data: HashMap<String, Any>
    ): Response<JsonObject>

    @POST
    suspend fun apiAuthFormDataQuery(
        @Header("Authorization") token: String,
        @Url url: String,
    ): Response<JsonObject>

    // @Headers(Constants.HEADER_API)
    @PUT
    suspend fun apiForPutData(
        @Header("Authorization") token: String,
        @Body data: HashMap<String, Any>,
        @Url url: String
    ): Response<JsonObject>

    @Headers(Constants.HEADER_API)
    @GET
    suspend fun apiGetOutWithQuery(
        @Header("Authorization") token: String,
        @Url url: String): Response<JsonObject>

    @Headers(Constants.HEADER_API)
    @GET
    suspend fun apiGetWithQuery(
        @Url url: String,
        @QueryMap data: HashMap<String, Any>
    ): Response<JsonObject>

    @Headers(Constants.HEADER_API)
    @GET
    suspend fun apiGetWithQuery(
        @Header("Authorization") token: String,
        @Url url: String,
        @QueryMap data: HashMap<String, Any>
    ): Response<JsonObject>


    @Headers(Constants.HEADER_API)
    @Multipart
    @JvmSuppressWildcards
    @PUT
    suspend fun apiForPutMultipart(
        @Url url: String,
        @Header("Authorization") token: String,
        @PartMap data: Map<String, RequestBody>,
        @Part parts: MultipartBody.Part?
    ): Response<JsonObject>

    @Headers(Constants.HEADER_API)
    @Multipart
    @JvmSuppressWildcards
    @POST
    suspend fun apiForPostMultipart(
        @Url url: String,
        @Header("Authorization") token: String,
        @PartMap data: Map<String, RequestBody>,
        @Part parts: MutableList<MultipartBody.Part>
    ): Response<JsonObject>

    @DELETE
    suspend fun apiForDelete(
        @Header("Authorization") token: String,
        @Url url: String,
        @QueryMap data: HashMap<String, Any>
    ): Response<JsonObject>

    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @POST
    suspend fun apiAuthRawBody(
        @Header("Authorization") token: String,
        @Body data: Any,
        @Url url: String
    ): Response<JsonObject>



}