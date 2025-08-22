package com.tech.perfumos.data.local

import android.content.SharedPreferences
import com.tech.perfumos.data.model.GetCountryData
import com.tech.perfumos.utils.getValue
import com.tech.perfumos.utils.saveValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tech.perfumos.ui.auth_folder.model.UserDataModel
import javax.inject.Inject
import androidx.core.content.edit
import com.tech.perfumos.ui.camera_perfume.model.PerfumeInfoModel
import com.tech.perfumos.ui.onboarding_folder.OnboardingModel
import com.tech.perfumos.ui.onboarding_folder.model.OnBoardingModel
import com.tech.perfumos.utils.Utils

class SharedPrefManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    object KEY {
        const val USER = "user"

        const val BEARER_TOKEN = "bearer_token"
        const val PROFILE_COMPLETED = "profile_completed"
        const val APPEARANCE_KEY = "appearance_key"
        const val LOCALE = "locale_key"
        const val TODAY_RECORD = "today_record"
        const val TODAY = "today"
        const val ANS = "ans"
        const val IS_FIRST = "is_first"
        const val DARK_ON = "is_dark"
        const val IS_FIRST_HOME = "is_first_home"
        const val IS_FIRST_ESTIMATE = "is_first_estimate"
        const val isOnbaordingComplete = "isOnbaordingComplete"
        const val isOnbaordingCompleteBool = "isOnbaordingCompleteBool"

        const val ComparePerfumeData = "CompareListData"

        const val USER_TOKEN = "userToken"
        const val USER_ID = "USER_ID"
        const val BOARDING_STEPS = "boarding_steps"
        const val ON_BOARDING = "onBoarding"

    }

    fun setDarkMode(dark: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY.DARK_ON, dark)
        editor.apply()
    }

    fun getDarkMode(): Boolean? {
        return sharedPreferences.getValue(KEY.DARK_ON, true)
    }


    fun saveIsFirst(isFirst: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY.IS_FIRST, isFirst)
        editor.apply()
    }

    fun getIsFirst(): Boolean? {
        return sharedPreferences.getValue(KEY.IS_FIRST, false)
    }

    fun saveIsFirstEstimate(isFirst: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY.IS_FIRST, isFirst)
        editor.apply()
    }
    fun getUserToken(): String? {
        return sharedPreferences.getValue(KEY.USER_TOKEN, null)
    }
   fun getUserID(): String? {
        return sharedPreferences.getValue(KEY.USER_ID, null)
    }

    fun saveUserToken(token : String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY.USER_TOKEN, token)
        editor.apply()
    }

    fun saveUserData(token : String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY.USER_ID, token)
        editor.apply()
    }


    fun getBoardingStep(): Int? {
        return sharedPreferences.getValue(KEY.BOARDING_STEPS, 0)
    }

    fun saveBoardingStep(step : Int) {
        sharedPreferences.edit {
            putInt(KEY.BOARDING_STEPS, step)
        }
    }

    private val gson = Gson()

    fun saveCountryList(key: String, list: List<GetCountryData>) {
        val editor = sharedPreferences.edit()
        val json = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
    }

    fun gateCountryList(key: String): List<GetCountryData> {
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            val type = object : TypeToken<List<GetCountryData>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }


    fun getIsFirstEstimate(): Boolean? {
        return sharedPreferences.getValue(KEY.IS_FIRST, false)
    }


    fun saveUser(bean: UserDataModel) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY.USER, Gson().toJson(bean))
        editor.apply()
    }

    fun getCurrentUser(): UserDataModel? {
        val s: String? = sharedPreferences.getString(KEY.USER, null)
        return Gson().fromJson(s, UserDataModel::class.java)
    }

    fun saveOnboardingData(bean: OnBoardingModel) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY.ON_BOARDING, Gson().toJson(bean))
        editor.apply()
    }

    fun getOnboardingData(): OnBoardingModel? {
        val s: String? = sharedPreferences.getString(KEY.ON_BOARDING, null)
        return Gson().fromJson(s, OnBoardingModel::class.java)
    }

   /* fun saveCompareList(bean: ArrayList<PerfumeInfoModel.PerfumeInfoData>) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY.ComparePerfumeData, Gson().toJson(bean))
        editor.apply()
    }

    fun getCompareList(): ArrayList<PerfumeInfoModel.PerfumeInfoData>? {
        val s: String? = sharedPreferences.getString(KEY.ComparePerfumeData, null)
        return Gson().fromJson(s, PerfumeInfoModel.PerfumeInfoData::class.java)
    }*/

    fun saveCompareList(bean: ArrayList<PerfumeInfoModel.PerfumeInfoData?>) {
        sharedPreferences.edit() {
            putString(KEY.ComparePerfumeData, Gson().toJson(bean))
        }
    }

    fun getCompareList(): ArrayList<PerfumeInfoModel.PerfumeInfoData>? {
        val s: String? = sharedPreferences.getString(KEY.ComparePerfumeData, null)
        if (s == null) return null

        val type = object : TypeToken<ArrayList<PerfumeInfoModel.PerfumeInfoData>>() {}.type
        return Gson().fromJson<ArrayList<PerfumeInfoModel.PerfumeInfoData>>(s, type)
    }




    fun setOnboardingComplete(jsonString: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY.isOnbaordingComplete, jsonString)
        editor.apply()
    }
    fun getOnboardingComplete() :String?{
        if(sharedPreferences.getString(KEY.isOnbaordingComplete, null)==null||sharedPreferences.getString(KEY.isOnbaordingComplete, null).equals("0")){
            return "1"
        }
        return sharedPreferences.getString(KEY.isOnbaordingComplete, null)
    }

    fun setOnboardingCompleteBool(jsonString: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY.isOnbaordingCompleteBool, jsonString)
        editor.apply()
    }
    fun getOnboardingCompleteBool() :Boolean {
        return sharedPreferences.getBoolean(KEY.isOnbaordingCompleteBool, false)
    }




    fun getStatus(): String? {
        return sharedPreferences.getValue(KEY.BEARER_TOKEN, "Open")
    }

    fun profileCompleted(isProfile: Boolean) {
        sharedPreferences.saveValue(KEY.PROFILE_COMPLETED, isProfile)
    }

    fun isProfileCompleted(): Boolean? {
        return sharedPreferences.getValue(KEY.PROFILE_COMPLETED, false)
    }

    fun setAppearance(type: Int) {
        sharedPreferences.saveValue(KEY.APPEARANCE_KEY, type)
    }

    fun getAppearance(): Int {
        return sharedPreferences.getInt(KEY.APPEARANCE_KEY, 0)
    }

    fun setLocaleType(type: String?) {
        sharedPreferences.saveValue(KEY.LOCALE, type)
    }

    fun getLocaleType(): String? {
        return sharedPreferences.getString(KEY.LOCALE, "en")
    }


    fun getToday(): Int {
        return sharedPreferences.getInt(KEY.TODAY, 0)
    }

    fun setToday(type: Int?) {
        sharedPreferences.saveValue(KEY.TODAY, type)
    }

    fun ansToday(): Int {
        return sharedPreferences.getInt(KEY.ANS, 0)
    }

    fun setAnsToday(type: Int?) {
        sharedPreferences.saveValue(KEY.ANS, type)
    }

    /* fun getToken(): String {
         return getCurrentUser()?.token?.let { token ->
             "Bearer $token"
         }.toString()
     }*/

    fun clear() {
        Utils.isCompleted = false
        sharedPreferences.edit().clear().apply()
    }
}