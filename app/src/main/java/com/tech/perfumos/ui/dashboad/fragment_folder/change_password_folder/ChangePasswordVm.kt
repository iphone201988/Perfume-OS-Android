package com.tech.perfumos.ui.dashboad.fragment_folder.change_password_folder

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
class ChangePasswordVm @Inject constructor(val apiHelper: ApiHelper):  BaseViewModel() {


    val commonObserver = SingleRequestEvent<JsonObject>()

    fun verifyOtpApi(url: String, data: HashMap<String, Any>) {
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

    fun changePasswordApi(url: String, data: HashMap<String, Any>) {
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

    fun resendApi(url: String, data: HashMap<String, Any>) {
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

}