package com.tech.perfumos.utils

import com.google.gson.Gson

data class Resource<out T>(val status: Status, val data: T?, val message: String?,
                           val code: Int? = null
) {

    companion object {

        fun <T> success(msg: String?,data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, msg)
        }

        fun <T> error(msg: String?, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }
        inline fun <reified T> errorBody(msg: okhttp3.ResponseBody?, data: T?): Resource<T> {
            val gson = Gson()
            val rawJson = msg?.string()
            if (isHtmlContent(rawJson)) {
                return Resource(Status.ERROR, null, "HTML content received")
            }
            val parsedData = gson.fromJson(rawJson, T::class.java)
            return Resource(Status.ERROR, parsedData, rawJson)
        }

        fun isHtmlContent(data: String?): Boolean {
            return data?.contains("<html>") == true || data?.contains("<!DOCTYPE html>") == true
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }

        inline fun <reified T> errorWithCode(msg: okhttp3.ResponseBody?, code: Int): Resource<T> {
           // return Resource(Status.ERROR, data, msg, code)
            val gson = Gson()
            val rawJson = msg?.string()
            if (isHtmlContent(rawJson)) {
                return Resource(Status.ERROR, null, "HTML content received", code)
            }
            val parsedData = gson.fromJson(rawJson, T::class.java)
            return Resource(Status.ERROR, parsedData, rawJson, code)
        }

        inline fun <reified T> errorBodyMsg(msg: okhttp3.ResponseBody?): Resource<T> {
            val gson = Gson()
            val rawJson = msg?.string()
            if (isHtmlContent(rawJson)) {
                return Resource(Status.ERROR, null, "HTML content received")
            }
            val parsedData = gson.fromJson(rawJson, T::class.java)
            return Resource(Status.ERROR, parsedData, rawJson)
        }

    }

}