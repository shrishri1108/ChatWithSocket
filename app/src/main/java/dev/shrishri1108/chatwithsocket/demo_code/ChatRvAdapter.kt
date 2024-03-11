package dev.shrishri1108.chatwithsocket.demo_code

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.shrishri1108.chatwithsocket.databinding.ItemMyConversationBinding
import dev.shrishri1108.chatwithsocket.databinding.ItemReceivedMessageBinding
import dev.shrishri1108.chatwithsocket.databinding.RepliedMessageLaysItemBinding


class ChatRvAdapter(
    var conversationList: List<DemoMessageModel>,
    val itemClickListen: ItemClickListen,
    val myUserids: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context
        if (viewType == 1) {
            val itemReceivedMessageBinding: ItemReceivedMessageBinding =
                ItemReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return ReceivedMessageViewHolder(itemReceivedMessageBinding)
        } else if (viewType == 3) {
            val repliedMessageLaysItemBinding: RepliedMessageLaysItemBinding =
                RepliedMessageLaysItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

            return MyRepliedMessageViewHolder(repliedMessageLaysItemBinding)
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
        fun onClicked(itemView: View, userItem: DemoMessageModel)
    }

    class ReceivedMessageViewHolder(val itemReceivedMessageBinding: ItemReceivedMessageBinding) :
        RecyclerView.ViewHolder(itemReceivedMessageBinding.root) {
        fun bindReceivedMessages(itemMessageModel: DemoMessageModel, itemlistenr: ItemClickListen) {
            itemMessageModel?.let { messageModel ->
                try {
                    itemReceivedMessageBinding.tvMessage.text = messageModel.msg
                    itemReceivedMessageBinding.tvLastTime.text = ""

//                if (messageModel.isActive) {
//                    itemReceivedMessageBinding.ivActive.setImageDrawable(mContext?.getDrawable(R.drawable.bg_tv_status_active))
//                } else {
//                    itemReceivedMessageBinding.ivActive.setImageDrawable(mContext?.getDrawable(R.drawable.bg_tv_status_inactive))
                    if (itemMessageModel.type.equals("Reaction")) {
                        itemReceivedMessageBinding.cvReactionLays.visibility = View.VISIBLE
                        itemReceivedMessageBinding.tvReaction.text =
                            "${
                                itemMessageModel.ReactionMessage
//                            .last()
                            }"
                    } else {
                        itemReceivedMessageBinding.cvReactionLays.visibility = View.GONE
                    }
//                }

//                itemReceivedMessageBinding.receivedMessageItemLays.setOnClickListener { views ->
//                    itemlistenr.onClicked(itemView, messageModel)
//                }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (conversationList[position].isReplyMessage == false) {
            if (conversationList[position].userId.equals(myUserids)) {
                return 2
            }
            return 1
        }
        return 3
    }


    class MyRepliedMessageViewHolder(val repliedMessageLaysItemBinding: RepliedMessageLaysItemBinding) :
        RecyclerView.ViewHolder(repliedMessageLaysItemBinding.root) {
        fun bindMyRepliedMessages(
            itemMessageModel: DemoMessageModel,
            itemClickListen: ItemClickListen,
            isFromYou: Boolean
        ) {
            try {
                itemMessageModel?.let { messageModel ->
                    repliedMessageLaysItemBinding.tvMessage.text = messageModel.msg
                    repliedMessageLaysItemBinding.tvLastTime.text = ""

                    if (messageModel.type.equals("Reaction")) {
                        repliedMessageLaysItemBinding.cvReactionLays.visibility = View.VISIBLE
                        repliedMessageLaysItemBinding.tvReaction.text =
                            "${
                                itemMessageModel.ReactionMessage
//                            .last()
                            }"
                    } else {
                        repliedMessageLaysItemBinding.cvReactionLays.visibility = View.GONE
                    }
                    repliedMessageLaysItemBinding.repliedMessageItemLays.visibility = View.VISIBLE
                    repliedMessageLaysItemBinding.tvReply.text =
                        "${
                            itemMessageModel.questionMessage
//                            .last()
                        }"

                    if (isFromYou == false)
                        repliedMessageLaysItemBinding.tvReplyMessageSenderName.text = "User 3"
//                if (messageModel.isActive) {
//                    repliedMessageLaysItemBinding.ivActive.setImageDrawable(mContext?.getDrawable(R.drawable.bg_tv_status_active))
//                } else {
//                    repliedMessageLaysItemBinding.ivActive.setImageDrawable(mContext?.getDrawable(R.drawable.bg_tv_status_inactive))
//                }
//
//                repliedMessageLaysItemBinding.repliedMessageItemLays.setOnLongClickListener {
//                    itemClickListen.onClicked(itemView, messageModel)
//                    true
//                }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }

    class MyMessageViewHolder(val itemMyConversationBinding: ItemMyConversationBinding) :
        RecyclerView.ViewHolder(itemMyConversationBinding.root) {
        fun bindMyMessages(itemMessageModel: DemoMessageModel, itemlistenr: ItemClickListen) {
            itemMessageModel?.let { messageModel ->
                try {
                    itemMyConversationBinding.tvMessage.text = messageModel.msg
                    itemMyConversationBinding.tvLastTime.text = ""

                    if (messageModel.type.equals("Reaction")) {
                        itemMyConversationBinding.cvReactionLays.visibility = View.VISIBLE
                        itemMyConversationBinding.tvReaction.text =
                            "${
                                itemMessageModel.ReactionMessage
//                            .last()
                            }"
                    } else {
                        itemMyConversationBinding.cvReactionLays.visibility = View.GONE
                    }


//                if (messageModel.isActive) {
//                    itemMyConversationBinding.ivActive.setImageDrawable(mContext?.getDrawable(R.drawable.bg_tv_status_active))
//                } else {
//                    itemMyConversationBinding.ivActive.setImageDrawable(mContext?.getDrawable(R.drawable.bg_tv_status_inactive))
//                }
//
//                itemMyConversationBinding.myMessageItemLays.setOnLongClickListener {
//                    itemlistenr.onClicked(itemView, messageModel)
//                    true
//                }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemMessageModel = conversationList[position]

        when (holder) {
            is MyMessageViewHolder -> {
                holder.bindMyMessages(itemMessageModel, itemClickListen)
            }

            is ReceivedMessageViewHolder -> {
                holder.bindReceivedMessages(itemMessageModel, itemClickListen)
            }

            is MyRepliedMessageViewHolder -> {
                holder.bindMyRepliedMessages(
                    itemMessageModel,
                    itemClickListen,
                    itemMessageModel.userId.equals(myUserids)
                )
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
    fun refreshList(newItems: List<DemoMessageModel>) {
        this.conversationList = newItems
        this.notifyDataSetChanged()
    }

    fun refreshItem(index: Int) {
        this.notifyItemChanged(index)
    }
}

