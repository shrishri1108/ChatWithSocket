package dev.shrishri1108.chatwithsocket.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.shrishri1108.chatwithsocket.SocketViewModel
import dev.shrishri1108.chatwithsocket.message.MessageModel
import io.socket.client.Socket

class UserListViewModel : ViewModel() {

    private val userListObj = MutableLiveData<List<UserModel>>()
    val userList: LiveData<List<UserModel>> get() = userListObj
    private lateinit var mSocketViewModel: SocketViewModel
    private val toastObj = MutableLiveData<String>("")
    val toastMessage: LiveData<String> get() = toastObj

    fun callUserListMessage() {
        mSocketViewModel.callEvent("conversation list")
    }

    fun updateOnlineStatus(updatedUserId: String, isOnline: Boolean) {
        userListObj.value?.forEachIndexed { index, userModel ->
            if (userModel.userId.equals(updatedUserId) == true && userModel.isActive != isOnline) {
                userModel.isActive = isOnline
            }
        }
    }

    fun setUpSocketViewModelInstance(socketViewModel: SocketViewModel) {
        this.mSocketViewModel = socketViewModel
        listenToConversationList()
    }

    fun listenToConversationList() {
        mSocketViewModel.listenEvent("listen conversation") {
            if (it[0] != null) {
                val newMessageList = it[0] as List<UserModel>?
                newMessageList?.let { userListObj.postValue(it) }
            }
        }
    }

    fun setUserList(userLists: ArrayList<UserModel>) {
        this.userListObj.postValue(userLists)
    }
}