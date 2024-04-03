package com.agnidating.agni.model
import com.google.gson.annotations.SerializedName

data class DataX(
    @SerializedName("created_on")
    var createdOn: String="",
    @SerializedName("message")
    var message: String,
    @SerializedName("MessageType")
    var messageType: String="text",
    @SerializedName("readStatus")
    var readStatus: String="0",
    @SerializedName("receiver_id")
    var receiverId: String="",
    @SerializedName("receiverName")
    var receiverName: String="",
    @SerializedName("receiver_profile_picture")
    var receiverProfilePicture: String="",
    @SerializedName("sender_id")
    var senderId: String="",
    @SerializedName("senderName")
    var senderName: String="",
    @SerializedName("flowerCount")
    var flowerCount: String="",
    @SerializedName("sender_profile_picture")
    var senderProfilePicture: String=""
)