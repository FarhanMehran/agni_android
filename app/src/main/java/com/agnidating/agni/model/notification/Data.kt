package com.agnidating.agni.model.notification


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Data(
    @SerializedName("created_at")
    var createdAt: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("notification_msg")
    var notificationMsg: String,
    @SerializedName("notification_title")
    var notificationTitle: String,
    @SerializedName("notification_type")
    var notificationType: String,
    @SerializedName("receiverId")
    var receiverId: String,
    @SerializedName("senderId")
    var senderId: String,
    @SerializedName("recieverprofileImg")
    var receiverImage: String,
    @SerializedName("senderprofileImg")
    var senderImage: String
) : Parcelable