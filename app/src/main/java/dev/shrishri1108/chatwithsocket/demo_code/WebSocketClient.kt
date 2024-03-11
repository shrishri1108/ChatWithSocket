package dev.shrishri1108.chatwithsocket.demo_code

import kotlinx.coroutines.*
import java.net.*
import java.nio.*
import kotlin.coroutines.*

class WebSocketClient(private val url: String) {
    private val socket = Socket(url, 3001)
    private val input = socket.getInputStream()
    private val output = socket.getOutputStream()

    suspend fun start(coroutineContext: CoroutineContext) = coroutineScope {
        launch(coroutineContext) {
            val buffer = ByteBuffer.allocate(1024)
            while (isActive) {
                val bytesRead = input.read(buffer.array())
                if (bytesRead > 0) {
                    val message = buffer.array().decodeToString(0, bytesRead)
                    println("Received: $message")
                    buffer.clear()
                }
            }
        }

        launch(coroutineContext) {
            val message = "Hello, WebSocket!"
            output.write(message.toByteArray())
            output.flush()
            delay(1000)
            socket.close()
        }
    }
}

fun main() = runBlocking {
    val client = WebSocketClient("http://192.168.1.21")
    client.start(this.coroutineContext)
}