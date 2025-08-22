package com.tech.perfumos.ui.dashboad.fragment_folder.home_folder

import android.util.Log
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
class HomeFragmentVm @Inject constructor(private val apiHelper: ApiHelper) : BaseViewModel() {
    val commonObserver = SingleRequestEvent<JsonObject>()

    fun getProfileApi(url :String) {
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

    fun getHomeFragmentApi(url :String) {
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
    fun updateData(url :String, data:HashMap<String, Any>) {
       // commonObserver.postValue(Resource.loading(null))
        Log.d("updateData", "updateData: ${data.get("tutorialProgess")}")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiHelper.apiForPutData(url, data)
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

    fun getBadgesApi(url :String) {
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
    fun getRanksApi(url :String) {
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

}