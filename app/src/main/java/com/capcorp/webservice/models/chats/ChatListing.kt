package com.capcorp.webservice.models.chats

import com.capcorp.webservice.models.ProfilePicUr
import com.capcorp.webservice.models.UserId


data class ChatListing(
    var receiverId: UserId?,
    var senderId: UserId?,
    var sentAt: Long?,
    var text: String?,
    var messageId: String?,
    var _id: String?,
    var unDeliverCount: Int?,
    var chatImage: ProfilePicUr?,
    var chatType: String?
)