package dev.shrishri1108.chatwithsocket

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketHandler {

    lateinit var mSocket: Socket
    @Synchronized
    fun setSocket() {
        try {
            mSocket = IO.socket(Constants.BASE_URL)
            mSocket = mSocket.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        mSocket.connect()
        Log.d(Constants.TAG, "Socket connection status: ${mSocket.connected()}")
    }

    @Synchronized
    fun closeConnection() {
        mSocket.disconnect()
        Log.d(Constants.TAG, "Socket connection status after disconnection: ${mSocket.connected()}")

    }
//
//    fun listenToConnection() {
//        mSocket.on("connect") {
//
//        }
//
//
//        mSocket.on("disconnect" ) {reason ->
//            if (reason.equals("io server disconnect") ) {
//                // the disconnection was initiated by the server, you need to reconnect manually
//                mSocket.connect();
//            }
//            // else the socket will automatically try to reconnect
//        }
//    }

}