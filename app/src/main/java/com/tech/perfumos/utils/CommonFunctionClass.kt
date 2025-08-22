package com.tech.perfumos.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.tech.perfumos.BuildConfig
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object CommonFunctionClass {
    fun <T, ID> singleSelectionRV(
        list: List<T>,
        selectedId: ID,
        getId: (T) -> ID,
        isSelectedGetter: (T) -> Boolean,
        isSelectedSetter: (item: T, isSelected: Boolean, selectedItem: T?) -> Unit,
        notifyChanged: (Int) -> Unit
    ): T? {
        var previousSelectedIndex = -1
        var currentSelectedIndex = -1
        var selectedItem: T? = null
        list.forEachIndexed { index, item ->
            val isSelected = getId(item) == selectedId

            if (isSelectedGetter(item)) previousSelectedIndex = index
            if (isSelected) {
                currentSelectedIndex = index
                selectedItem = item
            }
        }
        // Update selection now that we have selectedItem
        list.forEachIndexed { index, item ->
            val isSelected = getId(item) == selectedId
            isSelectedSetter(item, isSelected, selectedItem)
        }

        if (previousSelectedIndex != -1) notifyChanged(previousSelectedIndex)
        if (currentSelectedIndex != -1 && currentSelectedIndex != previousSelectedIndex)
            notifyChanged(currentSelectedIndex)

        return selectedItem
    }

    fun <T> updateItemInRecyclerView(
        recyclerView: RecyclerView,
        list: List<T>,
        position: Int,
        updateItem: (T) -> Unit,
        updateView: (RecyclerView.ViewHolder, T) -> Unit,
        notifyFallback: (Int) -> Unit
    ) {
        if (position !in list.indices) return
        Handler(Looper.getMainLooper()).postDelayed({


            val item = list[position]
            updateItem(item)

            val holder = recyclerView.findViewHolderForAdapterPosition(position)
            if (holder != null) {
                updateView(holder, item)
            } else {
                notifyFallback(position) // If view not visible, notify adapter
            }
        },100)

    }

    fun jsonMessage(response: ResponseBody?): String {
        return try {
            val obj = JSONObject((response ?: "").toString())
            obj.getString("message")
        } catch (e: Exception) {
            return "No message found"
        }

    }
    fun logPrint(tag:String?="DEBUG_LOG",response: String,type: Boolean?=false) {
        if (BuildConfig.DEBUG){
            val stackTrace = Throwable().stackTrace
            val element = stackTrace[1]
            val fileName = element.fileName
            val methodName = element.methodName
            val lineNumber = element.lineNumber
            val logMessage = "($fileName:$lineNumber) $methodName() →"
            if(type==false){
                Log.i(tag, logMessage)
                Log.i(tag, response)
            }
            else{
                printVeryLongJson(tag?:"", response)
            }

        }

    }
//    fun printVeryLongJson(tag: String, prettyJson: String) {
//        val lines = prettyJson.split("\n")
//        for (line in lines) {
//            Log.d(tag, line)
//        }
//    }
fun printVeryLongJson(tag: String, rawJson: String) {
    try {
        // Format JSON if possible
        val prettyJson = when {
            rawJson.trim().startsWith("{") -> JSONObject(rawJson).toString(4)
            rawJson.trim().startsWith("[") -> JSONArray(rawJson).toString(4)
            else -> rawJson
        }

        val maxLogSize = 4000
        var start = 0
        var chunkIndex = 1

        while (start < prettyJson.length) {
            val end = (start + maxLogSize).coerceAtMost(prettyJson.length)
            val part = prettyJson.substring(start, end)

            // 👇 Prefix with chunk index to avoid Logcat header repeating inside JSON
            Log.d(tag, "*************************************************************************\n$part")
//            Log.d(tag, "Chunk $chunkIndex:\n$part")

            start = end
            chunkIndex++
        }
    } catch (e: Exception) {
        Log.e(tag, "Invalid JSON", e)
        Log.d(tag, rawJson)
    }
}
/**    fun printVeryLongJson(tag: String, rawJson: String) {
        try {
            val prettyJson = when {
                rawJson.trim().startsWith("{") -> {
                    val jsonObject = JSONObject(rawJson)
                    jsonObject.toString(4) // 4 = indentation
                }
                rawJson.trim().startsWith("[") -> {
                    val jsonArray = JSONArray(rawJson)
                    jsonArray.toString(4)
                }
                else -> rawJson
            }

            // Split into chunks if very large (Logcat has a ~4000 char limit)
            val maxLogSize = 400000
            for (i in prettyJson.indices step maxLogSize) {
                val end = (i + maxLogSize).coerceAtMost(prettyJson.length)
                Log.d(tag, prettyJson.substring(i, end))
            }
        } catch (e: Exception) {
            Log.e(tag, "Invalid JSON", e)
            Log.d(tag, rawJson) // print raw if parsing fails
        }
    }*/
}