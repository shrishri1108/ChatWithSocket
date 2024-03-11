package dev.shrishri1108.chatwithsocket.message

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.shrishri1108.chatwithsocket.databinding.ItemMyConversationBinding
import dev.shrishri1108.chatwithsocket.databinding.ItemReceivedMessageBinding

class MessageListAdapter(
    var conversationList: List<MessageModel>,
    val itemClickListen: ItemClickListen,
    val userId: String?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        if (viewType == 1) {
            val itemReceivedMessageBinding: ItemReceivedMessageBinding =
                ItemReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return ReceivedMessageViewHolder(itemReceivedMessageBinding)
        } else {
            val itemMyConversationBinding: ItemMyConversationBinding =
                ItemMyConversationBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

            return MyMessageViewHolder(itemMyConversationBinding)
        }

    }


    interface ItemClickListen {
        fun onClicked(userItem: MessageModel)
    }

    class ReceivedMessageViewHolder(val itemReceivedMessageBinding: ItemReceivedMessageBinding) :
        ViewHolder(itemReceivedMessageBinding.root) {
        fun bindReceivedMessages(itemMessageModel: MessageModel, itemlistenr: ItemClickListen) {
            itemMessageModel?.let { messageModel ->
                itemReceivedMessageBinding.tvMessage.text = messageModel.message
                itemReceivedMessageBinding.tvLastTime.text = messageModel.updatedAt

//                if (messageModel.isActive) {
//                    itemReceivedMessageBinding.ivActive.setImageDrawable(mContext?.getDrawable(R.drawable.bg_tv_status_active))
//                } else {
//                    itemReceivedMessageBinding.ivActive.setImageDrawable(mContext?.getDrawable(R.drawable.bg_tv_status_inactive))
//                }

                itemReceivedMessageBinding.receivedMessageItemLays.setOnClickListener {
                    itemlistenr.onClicked(messageModel)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (!conversationList[position].from.equals(userId)) {
            return 1
        }
        return 2
    }


    class MyMessageViewHolder(val itemMyConversationBinding: ItemMyConversationBinding) :
        ViewHolder(itemMyConversationBinding.root) {
        fun bindMyMessages(itemMessageModel: MessageModel, itemlistenr: ItemClickListen) {
            itemMessageModel?.let { messageModel ->
                itemMyConversationBinding.tvMessage.text = messageModel.message
                itemMyConversationBinding.tvLastTime.text = messageModel.updatedAt

//                if (messageModel.isActive) {
//                    itemMyConversationBinding.ivActive.setImageDrawable(mContext?.getDrawable(R.drawable.bg_tv_status_active))
//                } else {
//                    itemMyConversationBinding.ivActive.setImageDrawable(mContext?.getDrawable(R.drawable.bg_tv_status_inactive))
//                }

                itemMyConversationBinding.myMessageItemLays.setOnClickListener {
                    itemlistenr.onClicked(messageModel)
                }
            }
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemMessageModel = conversationList[position]

        when (holder) {
            is MyMessageViewHolder -> {
                holder.bindMyMessages(itemMessageModel, itemClickListen)
            }

            is ReceivedMessageViewHolder -> {
                holder.bindReceivedMessages(itemMessageModel, itemClickListen)
            }
        }

    }

    override fun getItemCount(): Int {
        return conversationList.size
    }

//        fun changeOnlineStatus(item_pos: Int, isActive: Boolean) {
//            if (conversationList[item_pos].isActive != isActive) {
//                conversationList[item_pos].isActive = isActive
//                this.notifyItemChanged(item_pos)
//            }
//        }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshList(newItems: List<MessageModel>) {
        this.conversationList = newItems
        this.notifyDataSetChanged()
    }
}



