package com.agnidating.agni.utils

import com.agnidating.agni.model.DataX
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import com.google.gson.JsonObject

/**
 * Create by AJAY ASIJA on 05/10/2022
 */


fun getMessageJson(msg:String,senderId:Int,receiverId:Int):String{
    val message= JsonObject()
    message.addProperty("serviceType", "SingleChat")
    message.addProperty("userID", senderId).toString()
    message.addProperty("recieverID", receiverId)
    message.addProperty("msg", msg)
    message.addProperty("room", getRoomId(receiverId,senderId))
    message.addProperty("type", "Chat")
    message.addProperty("MessageType", "Text")
    message.addProperty("FlowerCount", "0")
    return message.toString()
}
fun getClearChatJson(senderId:Int,receiverId:Int):String{
    val message= JsonObject()
    message.addProperty("serviceType", "ClearChat")
    message.addProperty("user_id", senderId).toString()
    message.addProperty("other_user_id", receiverId)
    return message.toString()
}
fun getFlowerMessageJson(msg:String,senderId:Int,receiverId:Int,flowerCount:Int):String{
    val message= JsonObject()
    message.addProperty("serviceType", "SingleChat")
    message.addProperty("userID", senderId).toString()
    message.addProperty("recieverID", receiverId)
    message.addProperty("msg", msg)
    message.addProperty("room", getRoomId(receiverId,senderId))
    message.addProperty("type", "Chat")
    message.addProperty("MessageType", "Flower")
    message.addProperty("FlowerCount", flowerCount)
    message.toString().logs()
    return message.toString()
}
fun getReadMessagesJson(senderId:Int,receiverId:Int):String{
    val message= JsonObject()
    message.addProperty("serviceType", "SingleChat")
    message.addProperty("userID", senderId).toString()
    message.addProperty("recieverID", receiverId)
    message.addProperty("room", getRoomId(receiverId,senderId))
    message.addProperty("type", "read")
    message.addProperty("msgID", "")
    return message.toString()
}

fun getRoomId(id: Int, userId: Int): String {
    return if (id-userId<=0) "$id-$userId" else "$userId-$id"
}

fun getRecentChats(senderId:String): JsonObject {
    val message= JsonObject()
    message.addProperty("serviceType", "RecentChat")
    message.addProperty("userID", senderId).toString()
    return message
}

fun createSendMessageObject(msg:String,sharedPrefs: SharedPrefs): DataX {
    return DataX(
        createdOn=getCreatedOn(),
        message = msg,
        senderId = sharedPrefs.getString(CommonKeys.USER_ID).toString(),
        senderName = sharedPrefs.getString(CommonKeys.NAME).toString(),
    )
}