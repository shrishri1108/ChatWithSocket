package dev.shrishri1108.chatwithsocket

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject

class SocketViewModel : ViewModel() {

    private lateinit var mSocket: Socket
    private val isConnectedObj = MutableLiveData<Boolean>(false)
    val isConnected: LiveData<Boolean> get() = isConnectedObj

    fun setUpSocket() {
        SocketHandler.setSocket()
        mSocket = SocketHandler.getSocket()
        try {
//            mSocket.connect()
            SocketHandler.establishConnection()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        isConnectedObj.postValue(mSocket?.connected())
        Log.d(Constants.TAG, "isConnected: ${isConnected.value}")
    }


    fun listenEvent(eventName: String, emitterListener: Emitter.Listener) {
        if (isConnectedObj.value == true)
            mSocket.on(eventName, emitterListener)
    }

    fun callEvent(eventName: String, argsObject: JSONObject) {
        if (isConnectedObj.value == true)
            mSocket.emit(eventName, argsObject)
    }

    fun callEvent(eventName: String, argsObject: String) {
        if (isConnectedObj.value == true)
            mSocket.emit(eventName, argsObject)
    }

    fun callEvent(eventName: String) {
        if (isConnectedObj.value == true)
            mSocket.emit(eventName)
    }

    fun closeSocket() {
        mSocket.disconnect()
        SocketHandler.closeConnection()
    }
}