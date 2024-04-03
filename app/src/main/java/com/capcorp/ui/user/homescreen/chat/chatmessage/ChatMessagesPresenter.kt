package com.capcorp.ui.user.homescreen.chat.chatmessage

import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.chats.ChatMessageList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatMessagesPresenter : BasePresenterImpl<ChatMessageContract.View>(),
    ChatMessageContract.Presenter {

    private var call: Call<ApiResponse<ChatMessageList>>? = null

    override fun getChatMessagesApiCall(
        authorization: String,
        messageId: String,
        receiverId: String,
        limit: Int,
        skip: Int,
        messageOrder: String
    ) {
        if (call != null) {
            call?.cancel()
        }
        getView()?.showLoader(true)
        call = RestClient.get()
            .getChatMessages(authorization, messageId, receiverId, limit, skip, messageOrder)
        call?.enqueue(object : Callback<ApiResponse<ChatMessageList>> {
            override fun onFailure(call: Call<ApiResponse<ChatMessageList>>?, t: Throwable?) {
                getView()?.showLoader(false)
                getView()?.apiFailure()
            }

            override fun onResponse(
                call: Call<ApiResponse<ChatMessageList>>?,
                response: Response<ApiResponse<ChatMessageList>>?
            ) {
                getView()?.showLoader(false)
                if (response?.isSuccessful == true) {
                    if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                        getView()?.chatMessagesApiSuccess(
                            response.body()?.data?.chatListing,
                            messageOrder
                        )
                    } else {
                        getView()?.handleApiError(
                            response.body()?.statusCode,
                            response.body()?.message
                        )
                    }
                } else {
                    val errorModel = getApiError(response?.errorBody()?.string())
                    getView()?.handleApiError(errorModel.statusCode, errorModel.message)
                }
            }
        })
    }
}