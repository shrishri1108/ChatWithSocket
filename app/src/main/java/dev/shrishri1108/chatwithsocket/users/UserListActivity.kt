package dev.shrishri1108.chatwithsocket.users

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dev.shrishri1108.chatwithsocket.R
import dev.shrishri1108.chatwithsocket.SocketViewModel
import dev.shrishri1108.chatwithsocket.databinding.ActivityUserListBinding
import dev.shrishri1108.chatwithsocket.message.ChatActivity
import kotlinx.coroutines.launch
import java.io.Serializable

class UserListActivity : AppCompatActivity(), UserListAdapter.ItemClickListen {
    private lateinit var userListAdapter: UserListAdapter
    private lateinit var userListViewModel: UserListViewModel
    private lateinit var socketViewModel: SocketViewModel
    private lateinit var binding: ActivityUserListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@UserListActivity, R.layout.activity_user_list)

        userListViewModel = ViewModelProvider(
            this@UserListActivity,
            defaultViewModelProviderFactory
        )[UserListViewModel::class.java]

        socketViewModel = ViewModelProvider(
            this@UserListActivity,
            defaultViewModelProviderFactory
        )[SocketViewModel::class.java]
        socketViewModel.isConnected.observe(this@UserListActivity) {
            if (!it) {
                socketViewModel.setUpSocket()
                userListViewModel.setUpSocketViewModelInstance(socketViewModel)
            }
        }

        userListViewModel.toastMessage.observe(this@UserListActivity) {
            if (!it.isNullOrEmpty()) {
                Toast.makeText(this@UserListActivity, it, Toast.LENGTH_SHORT).show()
            }
        }

        userListAdapter = UserListAdapter(ArrayList<UserModel>(), this@UserListActivity)
        userListViewModel.userList.observe(this@UserListActivity) {
            if (it.isNullOrEmpty() == false) {
                userListAdapter.refreshList(it)
                binding.rvUserList.visibility = View.VISIBLE
                binding.noChats.visibility = View.GONE
            } else {
                binding.rvUserList.visibility = View.GONE
                binding.noChats.visibility = View.VISIBLE
            }
        }

        binding.rvUserList.adapter = userListAdapter



        var userLists = ArrayList<UserModel>()
        var i = 1
        while (i<10) {
            userLists.add(UserModel("user1","user 2", "This is demo message", "03:39 Pm", "roomId2", true))
            i++
        }
        userListViewModel.setUserList(userLists)

    }

    override fun onClicked(userItem: UserModel) {
        val intent = Intent(this@UserListActivity, ChatActivity::class.java)
        val bundles = Bundle()
        bundles.putSerializable("user", userItem )
        intent.putExtras(bundles)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        this@UserListActivity.lifecycleScope.launch {
            userListViewModel.callUserListMessage()
        }

    }

}