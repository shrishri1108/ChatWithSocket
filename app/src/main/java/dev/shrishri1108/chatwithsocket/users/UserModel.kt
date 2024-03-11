package dev.shrishri1108.chatwithsocket.users

import java.io.Serializable

data class UserModel(val userId:String, val userName:String, val lastMessage:String, val lastMessageTime:String,val roomId:String, var isActive:Boolean):Serializable
