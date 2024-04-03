package com.capcorp.webservice.models.chats

import com.capcorp.webservice.models.ProfilePicUr


data class ChatMessageListing(
    var _id: String?,
    var messageId: String?,
    var receiverId: String?,
    var senderId: String?,
    var text: String?,
    var chatImage: ProfilePicUr?,
    var sentAt: Long?,
    var chatType: String?,
    var isSent: Boolean?,
    var isDeliver: Boolean = false,
    var isRead: Boolean = false,
    var isFailed: Boolean? = false,
    var mediaToUpload: MediaUpload? = null
)

data class ChatDelivery(
    var _id: String?,
    var receiverId: String?,
    var senderId: String?
)