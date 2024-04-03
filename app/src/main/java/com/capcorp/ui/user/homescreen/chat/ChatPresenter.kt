package com.capcorp.ui.user.homescreen.chat


import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.chats.ChatListing
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatPresenter : BasePresenterImpl<ChatContract.View>(), ChatContract.Presenter {
    override fun getSearchUser(authorization: String, limit: Int, skip: Int, search: String) {
        RestClient.get().searchChat(authorization, limit, skip, search)
            .enqueue(object : Callback<ApiResponse<ArrayList<ChatListing>>> {
                override fun onFailure(
                    call: Call<ApiResponse<ArrayList<ChatListing>>>?,
                    t: Throwable?
                ) {
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<ArrayList<ChatListing>>>?,
                    response: Response<ApiResponse<ArrayList<ChatListing>>>?
                ) {
                    if (response?.isSuccessful == true) {
                        if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                            getView()?.chatSearchSuccess(response.body()?.data)
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

    override fun getChatLogsApiCall(authorization: String, limit: Int, skip: Int) {
        RestClient.get().getChatLogs(authorization, limit, skip)
            .enqueue(object : Callback<ApiResponse<ArrayList<ChatListing>>> {
                override fun onFailure(
                    call: Call<ApiResponse<ArrayList<ChatListing>>>?,
                    t: Throwable?
                ) {
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<ArrayList<ChatListing>>>?,
                    response: Response<ApiResponse<ArrayList<ChatListing>>>?
                ) {
                    if (response?.isSuccessful == true) {
                        if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                            getView()?.chatLogsApiSuccess(response.body()?.data)
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