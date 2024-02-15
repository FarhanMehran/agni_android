package com.agnidating.agni.model.recent


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("created_on")
    var createdOn: String,
    @SerializedName("hash")
    var hash: String,
    @SerializedName("message")
    var message: String,
    @SerializedName("MessageType")
    var messageType: String,
    @SerializedName("readStatus")
    var readStatus: String,
    @SerializedName("receiver_id")
    var receiverId: String,
    @SerializedName("receiverName")
    var receiverName: String,
    @SerializedName("reciever_profile_picture")
    var receiverProfilePicture: String,
    @SerializedName("recieverOfflineTime")
    var recieverOfflineTime: String,
    @SerializedName("sender_id")
    var senderId: String,
    @SerializedName("senderName")
    var senderName: String,
    @SerializedName("receiverSuspendedStatus")
    var receiverSuspendedStatus: String,
    @SerializedName("senderOfflineTime")
    var senderOfflineTime: String,
    @SerializedName("sender_profile_picture")
    var senderProfilePicture: String,
    @SerializedName("unreadCount")
    var unreadCount: Int,
    @SerializedName("BlockStatus")
    var BlockStatus: Int
)