package com.tech.perfumos.utils

import android.util.Log
import com.tech.perfumos.data.api.Constants
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

import java.net.URISyntaxException

object SocketManagerHelper {
    private var socket: Socket? = null
    private var isInitialized = false
    fun init(userId: String) {
        if (isInitialized && socket?.connected() == true) {
            Log.d("SocketManager", "⚡ Socket already connected with userId=$userId")
            return
        }
        try {
            val opts = IO.Options().apply {
                forceNew = true
                reconnection = true
                reconnectionAttempts = Int.MAX_VALUE
                reconnectionDelay = 5000
                transports = arrayOf("websocket")
                query = "userId=$userId"
            }

            if (socket == null) {
                socket = IO.socket(Constants.BASE_URL_SOCKET, opts)
                setupDefaultHandlers()
            }

            if (socket?.connected() == false) {
                socket?.connect()
                Log.d("SocketManager", "✅ Socket connecting with userId=$userId")
            }

            isInitialized = true
        } catch (e: URISyntaxException) {
            CommonFunctionClass.logPrint(tag = "SOCKET_ERROR", "ERROR->$e")
        }
    }


    private fun setupDefaultHandlers() {
        socket?.on(Socket.EVENT_CONNECT) {

            CommonFunctionClass.logPrint(tag = "SOCKET_MANAGER", "✅ Connected")
        }

        socket?.on(Socket.EVENT_DISCONNECT) {

            CommonFunctionClass.logPrint(tag = "SOCKET_MANAGER", "❌ Disconnected")
        }

        socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->

            CommonFunctionClass.logPrint(tag = "SOCKET_MANAGER", "⚠️ Connect error: ${args.joinToString()}")
        }
    }

    // ---- Emit custom event ----
    fun emitEvent(eventName: String, data: JSONObject) {
        if (socket?.connected() == true) {
            socket?.emit(eventName, data)
            CommonFunctionClass.logPrint(tag = "SOCKET_MANAGER", "Emit: $eventName → $data")

        } else {

            CommonFunctionClass.logPrint(tag = "SOCKET_MANAGER", "Emit failed, socket not connected")
        }
    }
    // ---- Listen for custom event ----
    fun listenEvent(eventName: String, callback: (Any) -> Unit) {
        socket?.on(eventName) { args ->
            if (args.isNotEmpty()) {
                val data = args[0]

                CommonFunctionClass.logPrint(tag = "SOCKET_MANAGER", "Received event: $eventName → $data [${data::class.java.simpleName}]")
                callback(data)
            } else {

                CommonFunctionClass.logPrint(tag = "SOCKET_MANAGER", "Received event: $eventName with no args")
            }
        }
    }
    fun disconnect() {
        socket?.disconnect()
        socket?.close()
        socket = null
        isInitialized = false

        CommonFunctionClass.logPrint(tag = "SOCKET_MANAGER", "Socket disconnected & cleaned up")
    }

    fun getSocket(): Socket? = socket
}
