package com.capcorp.ui.user.homescreen.chat.chatmessage

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.chats.ChatMessageListing

class ChatMessageContract {


    interface View : BaseView {
        fun chatMessagesApiSuccess(chatList: ArrayList<ChatMessageListing>?, messageOrder: String)
    }

    interface Presenter : BasePresenter<View> {
        fun getChatMessagesApiCall(
            authorization: String,
            messageId: String,
            receiverId: String,
            limit: Int,
            skip: Int,
            messageOrder: String
        )
    }
}