package dev.shrishri1108.chatwithsocket.demo_code

import io.socket.client.IO;
import io.socket.client.Socket;
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.URISyntaxException;

class SocketManager() {

    private var mSocket: Socket? = null

    init {
        try {
            mSocket = IO.socket("http://192.168.1.21:3001/")
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun isConnected(): Boolean {
        val isConnected = mSocket?.connected() ?: false
        println("Socket connection status: $isConnected")
        return isConnected
    }

    fun connect() {
        mSocket?.connect()
        println("Socket connection status: ${mSocket?.connected()}")
    }

    fun disconnect() {
        mSocket?.disconnect()
        println("Socket connection status after disconnection: ${mSocket?.connected()}")
    }

    fun onMessageReceived(functionName: String,listener: (String) -> Unit) {
        mSocket?.on(functionName) { args ->
            val message = args[0] as String
            listener.invoke(message)
        }
    }

    fun callCustomEvent(functionName: String, messageName: String) {
        mSocket?.emit(functionName, messageName)
    }
}