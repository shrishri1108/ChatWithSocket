package dev.shrishri1108.chatwithsocket.users

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.shrishri1108.chatwithsocket.R
import dev.shrishri1108.chatwithsocket.databinding.ItemuserchatBinding

class UserListAdapter(var userList: List<UserModel>, val itemClickListen: ItemClickListen) : RecyclerView.Adapter<UserListAdapter.ItemUserViewHolder>() {
    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemUserViewHolder {
        mContext = parent.context
        val itemUserChatBinding: ItemuserchatBinding =
            ItemuserchatBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ItemUserViewHolder(itemUserChatBinding)
    }

    interface ItemClickListen {
        fun onClicked(userItem: UserModel)
    }

    override fun onBindViewHolder(holder: ItemUserViewHolder, position: Int) {
        val itemUserModel = userList[position]
        itemUserModel?.let {userModel ->
            holder.bindings.tvUserName.text = userModel.userName
            holder.bindings.tvLastMessage.text = userModel.lastMessage
            holder.bindings.tvLastTime.text = userModel.lastMessageTime

            if (userModel.isActive) {
                holder.bindings.ivActive.setImageDrawable(mContext?.getDrawable(R.drawable.bg_tv_status_active))
            } else {
                holder.bindings.ivActive.setImageDrawable(mContext?.getDrawable(R.drawable.bg_tv_status_inactive))
            }

            holder.bindings.itemLays.setOnClickListener {
                itemClickListen.onClicked(userModel)
            }
        }


    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun changeOnlineStatus(item_pos: Int, isActive: Boolean) {
        if (userList[item_pos].isActive != isActive) {
            userList[item_pos].isActive = isActive
            this.notifyItemChanged(item_pos)
        }
    }

    class ItemUserViewHolder(val bindings: ItemuserchatBinding) : ViewHolder(bindings.root)

    @SuppressLint("NotifyDataSetChanged")
    fun refreshList(newItems: List<UserModel>) {
        this.userList = newItems
        this.notifyDataSetChanged()
    }
}


