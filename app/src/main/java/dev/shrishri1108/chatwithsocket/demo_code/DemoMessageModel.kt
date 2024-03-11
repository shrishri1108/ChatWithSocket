package dev.shrishri1108.chatwithsocket.demo_code

data class DemoMessageModel (val room:String,val msg:String = "", val type:String ="", val ReactionMessage: String = "",val userId: String, val msgId:String = "" ,val isReplyMessage:Boolean ,val questionMessage:String  )