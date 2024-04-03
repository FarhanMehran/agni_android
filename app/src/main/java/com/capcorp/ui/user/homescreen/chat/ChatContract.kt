package com.capcorp.ui.user.homescreen.chat

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.chats.ChatListing

class ChatContract {

    interface View : BaseView {
        fun chatLogsApiSuccess(chatList: ArrayList<ChatListing>?)
        fun chatSearchSuccess(chatList: ArrayList<ChatListing>?)

    }

    interface Presenter : BasePresenter<View> {
        fun getChatLogsApiCall(authorization: String, limit: Int, skip: Int)
        fun getSearchUser(authorization: String, limit: Int, skip: Int, search: String)

    }
}