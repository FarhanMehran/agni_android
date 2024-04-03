package com.capcorp.webservice.models.chats


data class ChatMessageList(
    var chatListing: ArrayList<ChatMessageListing>?,
    var chatCount: Int?
)