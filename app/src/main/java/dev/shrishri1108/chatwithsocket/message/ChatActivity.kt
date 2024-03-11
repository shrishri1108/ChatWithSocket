package dev.shrishri1108.chatwithsocket.message

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dev.shrishri1108.chatwithsocket.Constants
import dev.shrishri1108.chatwithsocket.R
import dev.shrishri1108.chatwithsocket.SocketViewModel
import dev.shrishri1108.chatwithsocket.users.UserModel
import dev.shrishri1108.chatwithsocket.databinding.ActivityChatBinding
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity(), MessageListAdapter.ItemClickListen {

    private lateinit var socketViewModel: SocketViewModel
    private lateinit var chatsViewModel: ChatsViewModel
    private lateinit var messageListAdapter: MessageListAdapter
    private var receivedUser: UserModel? = null
    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@ChatActivity, R.layout.activity_chat)

        val bundle = intent.extras
        if (bundle != null && bundle.containsKey("user")) {
            receivedUser = bundle.getSerializable("user") as UserModel

        }
        val userId =
//            this@ChatActivity.getSharedPreferences(Constants.MyPrefernces, Context.MODE_PRIVATE)
//                .getString(Constants.UserId, null)
        "user 1"

        socketViewModel = ViewModelProvider(
            this@ChatActivity,
            defaultViewModelProviderFactory
        )[SocketViewModel::class.java]

        chatsViewModel = ViewModelProvider(
            this@ChatActivity,
            defaultViewModelProviderFactory
        )[ChatsViewModel::class.java]

        socketViewModel.isConnected.observe(this@ChatActivity) {
            if (!it) {
                socketViewModel.setUpSocket()
                chatsViewModel.setSocketViewModelInstance(socketViewModel)
            }
        }

        chatsViewModel.toastMessage.observe(this@ChatActivity) {
            if (!it.isNullOrEmpty()) {
                Toast.makeText(this@ChatActivity, it, Toast.LENGTH_SHORT).show()
            }
        }
//        receivedUser?.let { chatsViewModel.joinRoom(it.roomId) }


        messageListAdapter =
            MessageListAdapter(ArrayList<MessageModel>(), this@ChatActivity, userId)
        chatsViewModel.chatList.observe(this@ChatActivity) {
            if (it.isNullOrEmpty() == false) {
                messageListAdapter.refreshList(it)
                binding.rvList.visibility = View.VISIBLE
                binding.noChats.visibility = View.GONE
            } else {
                binding.rvList.visibility = View.GONE
                binding.noChats.visibility = View.VISIBLE
            }
        }

        binding.btnSendMessage.setOnClickListener {
            binding.etInpMessage.text?.let {
                receivedUser?.roomId?.let { rmId ->
                    chatsViewModel.sendMessage(rmId, it.toString())
                }
            }
        }
        binding.rvList.adapter = messageListAdapter

        var userLists = ArrayList<MessageModel>()
        var i = 1
        while (i < 10) {
            userLists.add(
                MessageModel(
                    "user1",
                    "This is written forget messagesss " +
                            "\n .",
                    if(i%3==0) receivedUser!!.userId else userId,
                    "roomId2",
                    "03:39 Pm"

                )
            )
            i++
        }
        chatsViewModel.setUserList(userLists)
    }

    override fun onResume() {
        super.onResume()

        this@ChatActivity.lifecycleScope.launch {
            receivedUser?.roomId?.let { its ->
                chatsViewModel.getLatestMessages(its)
            }
        }
    }

    override fun onClicked(userItem: MessageModel) {

    }
}