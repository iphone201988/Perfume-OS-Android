package com.tech.perfumos.ui.dashboad

import com.google.gson.JsonObject
import com.tech.perfumos.data.api.ApiHelper
import com.tech.perfumos.ui.base.BaseViewModel
import com.tech.perfumos.utils.Resource
import com.tech.perfumos.utils.event.SingleRequestEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardVm @Inject constructor(private val apiHelper: ApiHelper):  BaseViewModel() {
    val commonObserver = SingleRequestEvent<JsonObject>()

    val value  = false
    fun getRecentTopPerfumeApi(url :String) {
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


    fun getPerfumeApi(url :String,data: HashMap<String, Any>) {
        // commonObserver.postValue(Resource.loading(null))
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


    fun searchPerfumeApi(url :String,data: HashMap<String, Any>) {
        // commonObserver.postValue(Resource.loading(null))
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

}