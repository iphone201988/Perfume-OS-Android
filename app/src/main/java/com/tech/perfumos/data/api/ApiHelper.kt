package com.tech.perfumos.data.api

import com.tech.perfumos.data.model.User
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface ApiHelper {
    suspend fun getUsers(): Response<List<User>>

    suspend fun apiForRawBody(request:HashMap<String, Any>,url: String): Response<JsonObject>
    suspend fun apiAuthRawBody(request:HashMap<String, Any>,url: String): Response<JsonObject>
    suspend fun apiAuthRawBody(url: String, request:Any): Response<JsonObject>
    suspend fun apiForPutData(url: String, request:HashMap<String, Any>): Response<JsonObject>
    suspend fun apiForFormData(data: HashMap<String, Any>,url: String): Response<JsonObject>
    suspend fun apiAuthFormData(data: HashMap<String, Any>,url: String): Response<JsonObject>
    suspend fun apiAuthFormDataQuery(url: String): Response<JsonObject>
    suspend fun apiGetOutWithQuery(url:String): Response<JsonObject>
//    suspend fun getDropDown(): Response<JsonObject>
    suspend fun apiGetWithQuery(data: HashMap<String, Any>,url: String): Response<JsonObject>
    suspend fun apiGetWithQuery(url: String, data: HashMap<String, Any>): Response<JsonObject>
//    suspend fun getCity(id: String): Response<JsonObject>
    suspend fun apiForPostMultipart(url: String,map: HashMap<String, RequestBody>,
                                 part: MutableList<MultipartBody.Part>): Response<JsonObject>
    suspend fun apiForDelete(url: String , data: HashMap<String, Any>): Response<JsonObject>

    suspend fun apiForPutMultipart(url: String,map: HashMap<String, RequestBody>,
                                 part: MultipartBody.Part?): Response<JsonObject>
}