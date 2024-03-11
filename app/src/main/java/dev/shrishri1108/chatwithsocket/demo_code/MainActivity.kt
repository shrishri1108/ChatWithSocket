package dev.shrishri1108.chatwithsocket.demo_code

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.shrishri1108.chatwithsocket.R
import dev.shrishri1108.chatwithsocket.SocketHandler
import dev.shrishri1108.chatwithsocket.databinding.ActivityMainBinding
import io.socket.client.Socket
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.random.Random


class MainActivity : AppCompatActivity(), ChatRvAdapter.ItemClickListen {

    private val isReplyAttachObj = MutableLiveData<Boolean>(false)
    private val isReplyAttachd: LiveData<Boolean> get() = isReplyAttachObj
    private var replyAttachedMessage: DemoMessageModel? = null
    private lateinit var reactionPopupWindow: PopupWindow
    private var tounched_Item_position: Int = -1
    private val currentRoomId: String = "roomId2"
    private val myID: String = "user 2"
    private val anotherUserid: String = "user 3"
    private lateinit var chatRvAdapter: ChatRvAdapter
    private lateinit var binding: ActivityMainBinding

    var isConnected = true
    private val myMessageListObj = MutableLiveData<List<DemoMessageModel>>(ArrayList())
    private val myMessageList: LiveData<List<DemoMessageModel>> get() = myMessageListObj
    private lateinit var mSocket: Socket
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

        SocketHandler.setSocket()
        mSocket = SocketHandler.getSocket()
        isConnected = mSocket.connected()
        if (isConnected) {
            binding.btnConnect.text = "Disconnect"
        } else {
            binding.btnConnect.text = "Connect"
        }


        mSocket.on("chat message") { args ->
            if (args[0] != null) {
                var counter = args[0] as JSONObject
                this@MainActivity.lifecycleScope.launch {
                    try {
                        if (counter.has("type")) {
                            myMessageList.value!!.forEachIndexed { index, demoMessageModel ->
                                if (demoMessageModel.msgId == counter.get("msgId").toString()) {

                                    val oldMessageList = myMessageList.value!!.toMutableList()
                                    oldMessageList[index] = DemoMessageModel(
                                        room = currentRoomId,
                                        msg = demoMessageModel.msg,
                                        type = counter.get("type").toString(),
                                        ReactionMessage = counter.get("ReactionMessage")
                                            .toString(),
                                        userId = demoMessageModel.userId,
                                        msgId = demoMessageModel.msgId,
                                        isReplyMessage = false,
                                        questionMessage = ""
                                    )

                                    myMessageListObj.postValue(oldMessageList.toList())
                                    chatRvAdapter.refreshItem(index)
                                    val lastPosition: Int = chatRvAdapter.getItemCount() - 1

                                    binding.rvChatList.scrollToPosition(lastPosition)
                                    return@forEachIndexed
                                }
                            }
                        } else {
                            val oldMessageList = (myMessageList.value ?: listOf()).toMutableList()
                            oldMessageList.add(
                                DemoMessageModel(
                                    room = currentRoomId,
                                    msg = counter.get("msg").toString(),
                                    type = "",
                                    userId = counter.get("userId").toString(),

                                    msgId = counter.get("msgId").toString(),
                                    isReplyMessage = false,
                                    questionMessage = ""
                                )
                            )
                            myMessageListObj.postValue(oldMessageList.toList())
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        }

        isReplyAttachd.observe(this@MainActivity) {
            if (replyAttachedMessage!= null)
            if (it) {
                binding.replyLay.visibility = View.VISIBLE
                binding.tvReply.text = replyAttachedMessage!!.msg
                if (!replyAttachedMessage!!.userId.equals(myID)) {
                    binding.tvReplyMessageSenderName.text = replyAttachedMessage!!.userId
                } else {
                    binding.tvReplyMessageSenderName.text = "You"
                }
                binding.replyLay.visibility = View.VISIBLE
            } else {
                binding.replyLay.visibility = View.GONE
            }
        }

        binding.ivClose.setOnClickListener {
            isReplyAttachObj.postValue(false)
            replyAttachedMessage = null
            binding.replyLay.visibility = View.GONE
        }
        this@MainActivity.runOnUiThread {
            mSocket.on("join room") { args ->
                if (args[0] != null) {
                    val counter = args[0]
                    Toast.makeText(this@MainActivity, "join room:->  $counter", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        chatRvAdapter = ChatRvAdapter(ArrayList(), this@MainActivity, myID)
        val layoutManager = LinearLayoutManager(this@MainActivity)
        layoutManager.stackFromEnd = true
        binding.rvChatList.layoutManager = layoutManager
        binding.rvChatList.adapter = chatRvAdapter
        val reactionSelectionView: View =
            LayoutInflater.from(this).inflate(R.layout.reaction_selection_lays, null)
        reactionPopupWindow = PopupWindow(
            reactionSelectionView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        val tvComedyReaction =
            reactionSelectionView.findViewById<com.google.android.material.textview.MaterialTextView>(
                R.id.tvComedyReaction
            )
        tvComedyReaction.setOnClickListener { sendReaction("😂") }
        val tvGoodReaction =
            reactionSelectionView.findViewById<com.google.android.material.textview.MaterialTextView>(
                R.id.tvGoodReaction
            )
        tvGoodReaction.setOnClickListener { sendReaction("👌") }
        val tvHappyReaction =
            reactionSelectionView.findViewById<com.google.android.material.textview.MaterialTextView>(
                R.id.tvHappyReaction
            )
        tvHappyReaction.setOnClickListener { sendReaction("😊") }
        val tvKeepReaction =
            reactionSelectionView.findViewById<com.google.android.material.textview.MaterialTextView>(
                R.id.tvKeepReaction
            )
        tvKeepReaction.setOnClickListener { sendReaction("👍") }
        val tvSadReaction =
            reactionSelectionView.findViewById<com.google.android.material.textview.MaterialTextView>(
                R.id.tvSadReaction
            )
        tvSadReaction.setOnClickListener { sendReaction("😔") }
//        val tvMoreReaction =
//            reactionSelectionView.findViewById<AXEmojiEditText>(
//                R.id.tvMoreReaction
//            )
//        tvMoreReaction.setOnClickListener {
//            showEmojiKeyboard(tvSadReaction)
//            val textlistenr = object : TextWatcher {
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                    // Called before the text is changed
//                }
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    // Called when the text is changed
//                    val input = s.toString()
//                    sendReaction(input)
//                    tvSadReaction.removeTextChangedListener(textlistenr)
//                }
//
//                override fun afterTextChanged(s: Editable?) {
//                    // Called after the text is changed
//                }
//            }
//        tvSadReaction.addTextChangedListener(textlistenr)
//        }
//
//        binding.rvChatList.addOnItemTouchListener(object : OnItemTouchListener {
//            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//                var touchX = 0
//                var touchY = 0
//                when (e.action) {
//                    MotionEvent.ACTION_DOWN -> {
//                        touchX = e.x.toInt()
//                        touchY = e.y.toInt()
//                    }
//                    MotionEvent.ACTION_UP -> {
//                        val x = e.x.toInt()
//                        val y = e.y.toInt()
//
//                        // Check if the touch event was a long press
//                        if (Math.abs(x - touchX) < ViewConfiguration.getLongPressTimeout() &&
//                            Math.abs(y - touchY) < ViewConfiguration.getLongPressTimeout()
//                        ) {
//                            val childView = rv.findChildViewUnder(x.toFloat(), y.toFloat())
//                            val viewHolder = rv.getChildViewHolder(childView!!)
//                            if (viewHolder != null) {
//                                tounched_Item_position = viewHolder.adapterPosition
//                                showReactionSelectionList(x, y, reactionPopupWindow)
//                            }
//                        }
//                    }
//                }
////
////                if (e.action == MotionEvent.ACTION_DOWN) {
////                    val x = e.x.toInt()
////                    val y = e.y.toInt()
////
////                    val childView = rv.findChildViewUnder(x.toFloat(), y.toFloat())
////                    val viewHolder = rv.getChildViewHolder(childView!!)
////
////                    if (viewHolder != null) {
////                        tounched_Item_position = viewHolder.adapterPosition
////                        showReactionSelectionList(x, y, reactionPopupWindow)
////                    }
////                }
//                return false
//            }
//
//            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
//            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
//        })

//        binding.rvChatList.setOnClickListener(View.OnClickListener { v -> // Get the coordinates of the click event relative to chatRecyclerView
//            val location = IntArray(2)
//            v.getLocationOnScreen(location)
//            val x = location[0]
//            val y = location[1]
//
//            tounched_Item_position = binding.rvChatList.getChildAdapterPosition(v)
//
//            showReactionSelectionList(x, y, reactionPopupWindow)
//        })
//
//        EmojiManager.install(
//            GoogleEmojiProvider()
//        )

//        val tvMoreReaction = findViewById<EmojiEditText>(R.id.tvMoreReaction)
//        val llReactionss = findViewById<LinearLayout>(R.id.llReactions)
//
//        val popup = EmojiPopup.Builder
//            .fromRootView(findViewById<ConstraintLayout>(R.id.clReaction)).build(tvMoreReaction)
//
//        tvMoreReaction.setOnClickListener {
//            popup.toggle()
//            llReactionss.addView(getEmojiTextView(llReactionss, tvMoreReaction))
//            tvMoreReaction.text?.clear()
//        }

        binding.rvChatList.addOnItemTouchListener(
            RecyclerItemClickListener(this@MainActivity, binding.rvChatList,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(itemView: View?, position: Int) {
                        tounched_Item_position = itemView?.let {
                            binding.rvChatList.getChildAdapterPosition(
                                it
                            )
                        }!!
                        val location = IntArray(2)
                        itemView.getLocationOnScreen(location)
                        val x = (location[0] + itemView.width / 2) - 4
                        val y = (location[1] + itemView.height / 2) - 1

//        val x = location[0] + view.width / 2
//        val y = location[1] + view.height / 2
                        showReactionSelectionList(x, y, reactionPopupWindow)
                    }

                    override fun onSwipeLeft(view: View?, position: Int) {
                        showReplymessageInput(position)
                    }

                    override fun onSwipeRight(view: View?, position: Int) {
                        showReplymessageInput(position)
                    }
                })
        )

        myMessageList.observe(this@MainActivity) {
            chatRvAdapter.refreshList(it)
            val lastPosition: Int = chatRvAdapter.getItemCount() - 1

            binding.rvChatList.scrollToPosition(lastPosition)
        }
        binding.btnConnect.setOnClickListener {
            if (isConnected) {
                mSocket = mSocket.disconnect()
            } else {
                mSocket = mSocket.connect()
            }
            if (mSocket.connected()) {
                isConnected = true
                binding.btnConnect.text = "Disconnect"
            } else {
                isConnected = false
                binding.btnConnect.text = "Connect"
            }
            Log.d("TAG", "onCreate: ${isConnected}")
        }
        binding.btnJoinRoom.setOnClickListener {
            if (isConnected)
                mSocket.emit("join room", currentRoomId)
            else
                Toast.makeText(this@MainActivity, " First connect to Socket.", Toast.LENGTH_SHORT)
                    .show()
            isConnected = mSocket.connected()
            Log.d("TAG", "onCreate: ${isConnected}")
        }

        binding.btnClearMessage.setOnClickListener {
            myMessageListObj.postValue(ArrayList())
        }
        binding.btnSendMessage.setOnClickListener {
            if (!binding.etMessage.text.isNullOrEmpty()) {
                if (mSocket.connected()) {
                    val jsonMessage: JSONObject = JSONObject()
                    jsonMessage.put("room", currentRoomId)
                    jsonMessage.put("msg", binding.etMessage.text.toString())
                    jsonMessage.put("userId", myID)
                    jsonMessage.put("msgId", "msgIds:- ${myMessageList.value?.size}")

                    mSocket.emit("chat message", jsonMessage)
                    val presentMessageList = myMessageListObj.value as ArrayList<DemoMessageModel>
                    val isReplied = isReplyAttachd.value ?: false
                    val replyAttachdMessage = if (replyAttachedMessage!= null) {replyAttachedMessage!!.msg} else {""}
                    presentMessageList.add(
                        DemoMessageModel(
                            room = jsonMessage.get("room").toString(),
                            msg = jsonMessage.get("msg").toString(),
                            msgId = jsonMessage.get("msgId").toString(),
                            userId = jsonMessage.get("userId").toString(),
                            isReplyMessage = isReplied,
                            questionMessage = replyAttachdMessage
                        )
                    )
                    myMessageListObj.postValue(presentMessageList)

                    binding.etMessage.text = null
                    if (isReplyAttachd.value == true) {
                        isReplyAttachObj.postValue(false)
                    }

                } else
                    Toast.makeText(
                        this@MainActivity,
                        " First connect to Socket.",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                isConnected = mSocket.connected()
            }
        }

        showEmojis()
    }

    private fun showEmojis() {
//
//        val view = findViewById<EmojiPickerView>(R.id.emoji_picker)
//        view.setOnEmojiPickedListener {
//            findViewById<EditText>(R.id.edit_text).append(it.emoji)
//        }
//
//        findViewById<ToggleButton>(R.id.toggle_button)
//            .setOnCheckedChangeListener { _, isChecked ->
//                if (isChecked) {
//                    view.emojiGridColumns = 8
//                    view.emojiGridRows = 8.3f
//                } else {
//                    view.emojiGridColumns = 9
//                    view.emojiGridRows = 15f
//                }
//            }
//        findViewById<Button>(R.id.button).setOnClickListener {
//            view.setRecentEmojiProvider(
//                RecentEmojiProviderAdapter(CustomRecentEmojiProvider(applicationContext))
//            )
//        }
//        findViewById<ToggleButton>(R.id.activity_button)
//            .setOnCheckedChangeListener { _, isChecked ->
//                if (isChecked) {
//                    val intent = Intent(this, ComposeActivity::class.java)
//                    startActivity(intent)
//                } else {
//                    val intent = Intent(this, MainActivity::class.java)
//                    startActivity(intent)
//                }
//            }
    }

    private fun showReplymessageInput(message_position_to_be_replied: Int) {
        replyAttachedMessage = myMessageList.value?.get(message_position_to_be_replied)
        isReplyAttachObj.postValue(true)
    }

    private fun showEmojiKeyboard(editText: EditText) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)

        // Show the emoji keyboard by directly inserting an emoji

        // Show the emoji keyboard by directly inserting an emoji
        val inputConnection = editText.onCreateInputConnection(EditorInfo())
        inputConnection?.commitText("\uD83D\uDE0A", 1)
    }

    // Method to hide emoji keyboard
    private fun hideEmojiKeyboard(editText: EditText) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0)
    }

    // Method to check if the text contains an emoji
    private fun containsEmoji(text: String): Boolean {
        // Implement your logic to check for emojis
        // For simplicity, this example just checks for a smiley emoji
        return text.contains("😊")
    }
    private fun showReactionSelectionList(x: Int, y: Int, reactionPopupWindow: PopupWindow) {
        reactionPopupWindow.showAtLocation(binding.rvChatList, Gravity.NO_GRAVITY, x, y)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()
    }

//    override fun onClicked(userItem: DemoMessageModel) {
//        sendReaction(userItem)
//    }

    fun showEmojiKeyboard(editText: com.google.android.material.textview.MaterialTextView) {
        editText.inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE or
                InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS

        val imm =
            editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun sendReaction(rDemoMsgMdel: DemoMessageModel) {
        if (mSocket.connected()) {
            val jsonMessage: JSONObject = JSONObject()
            val demoMessage = DemoMessageModel(
                room = rDemoMsgMdel.room,
                msg = rDemoMsgMdel.msg,
                ReactionMessage = when (Random.nextInt(4)) {
                    1 -> "😊"
                    2 -> "😂"
                    3 -> "😍"
                    else -> "💕💕"
                },
                type = "Reaction",
                msgId = rDemoMsgMdel.msgId,
                userId = rDemoMsgMdel.userId,
                isReplyMessage = false,
                questionMessage = ""
            )

            try {
                val presentMessageList = myMessageList.value as ArrayList<DemoMessageModel>
                presentMessageList.forEachIndexed { index, demoMessageModel ->
                    if (demoMessageModel.msgId == rDemoMsgMdel.msgId) {
                        presentMessageList.add(
                            index,
                            demoMessage
                        )

                        presentMessageList.removeAt(index + 1)
                        myMessageListObj.postValue(presentMessageList)
                        chatRvAdapter.refreshItem(index)
                        val lastPosition: Int = chatRvAdapter.getItemCount() - 1

                        binding.rvChatList.scrollToPosition(lastPosition)
                        return@forEachIndexed
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            jsonMessage.put("room", demoMessage.room)
            jsonMessage.put("msg", demoMessage.msg)
            jsonMessage.put("ReactionMessage", demoMessage.ReactionMessage)
            jsonMessage.put("type", demoMessage.type)
            jsonMessage.put("msgId", rDemoMsgMdel.msgId)
            jsonMessage.put("userId", demoMessage.userId)

            mSocket.emit("chat message", jsonMessage)

        } else
            Toast.makeText(this@MainActivity, " First connect to Socket.", Toast.LENGTH_SHORT)
                .show()

        isConnected = mSocket.connected()
    }

    private fun sendReaction(msgReaction: String) {
        if (tounched_Item_position < myMessageList.value?.size!!) {
            val rDemoMsgMdel = myMessageList.value?.get(tounched_Item_position) as DemoMessageModel
            if (mSocket.connected()) {
                val jsonMessage: JSONObject = JSONObject()
                val demoMessage = DemoMessageModel(
                    room = rDemoMsgMdel.room,
                    msg = rDemoMsgMdel.msg,
                    ReactionMessage = msgReaction,
                    type = "Reaction",
                    msgId = rDemoMsgMdel.msgId,
                    userId = rDemoMsgMdel.userId,
                    isReplyMessage = rDemoMsgMdel.isReplyMessage,
                    questionMessage = rDemoMsgMdel.questionMessage
                )

                try {
                    val presentMessageList = myMessageList.value as ArrayList<DemoMessageModel>
                    presentMessageList.forEachIndexed { index, demoMessageModel ->
                        if (demoMessageModel.msgId == rDemoMsgMdel.msgId) {
                            presentMessageList.add(
                                index,
                                demoMessage
                            )

                            presentMessageList.removeAt(index + 1)
                            myMessageListObj.postValue(presentMessageList)
                            chatRvAdapter.refreshItem(index)
                            val lastPosition: Int = chatRvAdapter.getItemCount() - 1

                            binding.rvChatList.scrollToPosition(lastPosition)
                            return@forEachIndexed
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                jsonMessage.put("room", demoMessage.room)
                jsonMessage.put("msg", demoMessage.msg)
                jsonMessage.put("ReactionMessage", demoMessage.ReactionMessage)
                jsonMessage.put("type", demoMessage.type)
                jsonMessage.put("msgId", rDemoMsgMdel.msgId)
                jsonMessage.put("userId", demoMessage.userId)

                mSocket.emit("chat message", jsonMessage)

            } else
                Toast.makeText(this@MainActivity, " First connect to Socket.", Toast.LENGTH_SHORT)
                    .show()

        }
        isConnected = mSocket.connected()
    }

    override fun onClicked(itemView: View, userItem: DemoMessageModel) {
        tounched_Item_position = binding.rvChatList.getChildAdapterPosition(
            itemView
        )
        val location = IntArray(2)
        itemView.getLocationOnScreen(location)
        val x = (location[0] + itemView.width / 2) - 4
        val y = (location[1] + itemView.height / 2) - 1

//        val x = location[0] + view.width / 2
//        val y = location[1] + view.height / 2
        showReactionSelectionList(x, y, reactionPopupWindow)
    }

}

