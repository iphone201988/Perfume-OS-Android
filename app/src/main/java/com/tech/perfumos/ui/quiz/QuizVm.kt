package com.tech.perfumos.ui.quiz

import com.google.gson.JsonObject
import com.tech.perfumos.data.api.ApiHelper
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.utils.Resource
import com.tech.perfumos.utils.Status
import com.tech.perfumos.utils.event.SingleRequestEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizVm @Inject constructor(private val apiHelper: ApiHelper): BaseViewModel() {
    val commonObserver = SingleRequestEvent<JsonObject>()

    fun getFollowersApi(url: String, data: HashMap<String, Any>) {
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

    fun getQuizCategory(url :String,data: HashMap<String, Any>) {
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

    fun getLeaderBoardApi(url: String, data: HashMap<String, Any>) {
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
    fun getCheckUserEligibleApi(url: String) {
        commonObserver.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiHelper.apiGetOutWithQuery(url)
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

    fun createQuizApi(url :String,data: HashMap<String, Any>) {
        commonObserver.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiHelper.apiAuthFormData(data, url)
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

    fun sendInvite(url :String,data: Any) {
        commonObserver.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiHelper.apiAuthRawBody(url, data)
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


    fun submitQuizApi(url :String,data: Any) {
        commonObserver.postValue(Resource.loading(null))
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiHelper.apiAuthRawBody(url, data)
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

}