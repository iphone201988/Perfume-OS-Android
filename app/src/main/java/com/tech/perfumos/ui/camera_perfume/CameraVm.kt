package com.tech.perfumos.ui.camera_perfume

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.tech.perfumos.data.api.ApiHelper
import com.tech.perfumos.data.api.Constants
import com.tech.perfumos.data.api.OpenAIService
import com.tech.perfumos.data.model.Message
import com.tech.perfumos.data.model.OpenAIRequest
import com.tech.perfumos.data.model.OpenAIResponse
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.utils.Resource
import com.tech.perfumos.utils.event.SingleRequestEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

@HiltViewModel
class CameraVm @Inject constructor(val apiHelper: ApiHelper) : BaseViewModel() {

    val commonObserver = SingleRequestEvent<JsonObject>()

    fun getPerfumeApi(url :String,data: HashMap<String, Any>) {
        commonObserver.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiHelper.apiGetWithQuery(url, data)
                if (response.isSuccessful && response.body() != null) {
                    commonObserver.postValue(Resource.success(url, response.body()))
                } else {
                    val data = JsonObject()
                    data.addProperty("apiCall", url)
                    commonObserver.postValue(Resource.error("error", data))
                    //commonObserver.postValue(Resource.errorBodyMsg(response.errorBody()))
                }
            } catch (e: java.lang.Exception) {
                val data = JsonObject()
                data.addProperty("apiCall", url)
                commonObserver.postValue(Resource.error(e.message, data))
            }
        }
    }

    fun getPerfumerApi(url :String,data: HashMap<String, Any>) {
        commonObserver.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiHelper.apiGetWithQuery(url, data)
                if (response.isSuccessful && response.body() != null) {
                    commonObserver.postValue(Resource.success(url, response.body()))
                } else {
                      commonObserver.postValue(Resource.errorWithCode(response.errorBody(),  response.code()))
                }
            } catch (e: java.lang.Exception) {
                commonObserver.postValue(Resource.error(e.message, null))
            }
        }
    }

    fun addToCollectionApi(url: String, data: HashMap<String, Any>) {
        commonObserver.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiHelper.apiAuthRawBody(data, url)
                if (response.isSuccessful && response.body() != null) {
                    commonObserver.postValue(Resource.success("ADD_TO_COLLECTION", response.body()))
                } else {
                      commonObserver.postValue(Resource.errorWithCode(response.errorBody(),  response.code()))
                }
            } catch (e: java.lang.Exception) {
                commonObserver.postValue(Resource.error(e.message, null))
            }
        }
    }

    fun addToWishlistApi(url: String, data: HashMap<String, Any>) {
        commonObserver.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiHelper.apiAuthRawBody(data, url)
                if (response.isSuccessful && response.body() != null) {
                    commonObserver.postValue(Resource.success("ADD_TO_WISHLIST", response.body()))
                } else {
                      commonObserver.postValue(Resource.errorWithCode(response.errorBody(),  response.code()))
                }
            } catch (e: java.lang.Exception) {
                commonObserver.postValue(Resource.error(e.message, null))
            }
        }
    }

    fun addToFavoriteApi(url: String, data: HashMap<String, Any>) {
        commonObserver.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiHelper.apiAuthRawBody(data, url)
                if (response.isSuccessful && response.body() != null) {
                    commonObserver.postValue(Resource.success(url, response.body()))
                } else {
                    commonObserver.postValue(Resource.errorWithCode(response.errorBody(),  response.code()))
                }
            } catch (e: java.lang.Exception) {
                commonObserver.postValue(Resource.error(e.message, null))
            }
        }
    }


    fun postReviewApi(url: String, data: HashMap<String, Any>) {
        Log.d("hashMap", "postReviewApi: ${Gson().toJson(data)}")
        commonObserver.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiHelper.apiAuthRawBody(data, url)
                if (response.isSuccessful && response.body() != null) {
                    commonObserver.postValue(Resource.success(url, response.body()))
                } else {

                      commonObserver.postValue(Resource.errorWithCode(response.errorBody(),  response.code()))
                }
            } catch (e: java.lang.Exception) {
                val data = JsonObject()
                commonObserver.postValue(Resource.error(e.message, null))
            }
        }
    }

    fun getNotesApi(url :String,data: HashMap<String, Any>) {
        commonObserver.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiHelper.apiGetWithQuery(url, data)
                if (response.isSuccessful && response.body() != null) {
                    commonObserver.postValue(Resource.success(url, response.body()))
                } else {
                    commonObserver.postValue(Resource.errorWithCode(response.errorBody(),  response.code()))
                }
            } catch (e: java.lang.Exception) {
                commonObserver.postValue(Resource.error(e.message, null))
            }
        }
    }
    fun askChatGPTForPerfumeName(extractedText: String, onResult: (String?) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(OpenAIService::class.java)

        /*val prompt = """
        You are a perfume name detector. Given a block of text, extract and return only the **perfume name** (e.g., "Dior Sauvage") if it exists.
        If no perfume name is found, return exactly: null

        Text: "$extractedText"
    """.trimIndent()*/

        val prompt = ""

        val request = OpenAIRequest(
            messages = listOf(
                //Message("user", prompt)
                Message(
                    "system", """
            You are a perfume name detector. Given noisy OCR text from an image, your job is to detect and return the most likely real perfume name in it.
            Compare the text against known perfume names (e.g., Ambre & Tonka, Dior Sauvage, La Nuit de L’Homme, Bleu de Chanel, etc).
            If there's a partial or close match, return the full correct perfume name.
            If no real perfume name can be matched, return exactly: null
    
            Only return the perfume name, with correct capitalization and punctuation.
        """.trimIndent()
                ),
                Message("user", "OCR Text: $extractedText")
            )
        )

        val apiKey = "Bearer ${Constants.OPER_AI_KEY}" // Replace with your actual OpenAI API Key

        service.getPerfumeName(apiKey, request).enqueue(object : Callback<OpenAIResponse> {
            override fun onResponse(
                call: Call<OpenAIResponse>,
                response: Response<OpenAIResponse>
            ) {
                val result = response.body()?.choices?.firstOrNull()?.message?.content?.trim()
                onResult(if (result == "null") null else result)
            }

            override fun onFailure(call: Call<OpenAIResponse>, t: Throwable) {
                onResult(null)
            }
        })
    }
}