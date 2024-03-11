package dev.shrishri1108.chatwithsocket.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import dev.shrishri1108.chatwithsocket.SocketViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class ChatsViewModel : ViewModel() {
    private lateinit var mSocketViewModel: SocketViewModel
    private val chatListObj = MutableLiveData<List<MessageModel>>()
    val chatList: LiveData<List<MessageModel>> get() = chatListObj
    private val toastObj = MutableLiveData<String>("")
    val toastMessage: LiveData<String> get() = toastObj
    fun setSocketViewModelInstance(socketViewModel: SocketViewModel) {
        this.mSocketViewModel = socketViewModel
        listenChatMessages()
    }


    fun sendMessage(roomId: String, message: String) {
        if (mSocketViewModel.isConnected.value == true) {
            val jsonMessage: JSONObject = JSONObject()
            jsonMessage.put("room", roomId)
            jsonMessage.put("msg", message)

            mSocketViewModel.callEvent("chat message", jsonMessage)
        }
    }

    fun listenChatMessages() {
        mSocketViewModel.listenEvent("all chats") { args ->
            if (args[0] != null) {
                val newMessageList = args[0] as List<MessageModel>?
                newMessageList?.let { chatListObj.postValue(it) }
            }
        }
    }

    fun joinRoom(roomId: String) {
        mSocketViewModel.callEvent("join room", roomId)
    }

    fun getLatestMessages(roomId: String) {
        mSocketViewModel.callEvent("latest messages", roomId)
    }

    fun setUserList(conversationLists : ArrayList<MessageModel>) {
        this.chatListObj.postValue(conversationLists)
    }

}