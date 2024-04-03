package com.capcorp.ui.user.homescreen.Anys.add_receiver

import com.capcorp.ui.user.homescreen.orders.add_receiver.ReceiverContract
import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.request_model.AddReciverRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReceiverPresenter : BasePresenterImpl<ReceiverContract.View>(), ReceiverContract.Presenter {
    override fun apiAddReciver(accessToken: String, addReceiverDetail: AddReciverRequest) {
        getView()?.showLoader(true)
        RestClient.get().addReceiverInfo(accessToken, addReceiverDetail)
            .enqueue(object : Callback<ApiResponse<Any>> {
                override fun onResponse(
                    call: Call<ApiResponse<Any>>,
                    response: Response<ApiResponse<Any>>
                ) {
                    getView()?.showLoader(false)
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == 200) {
                            getView()?.apiSuccess()
                        } else {
                            getView()?.handleApiError(
                                response.body()?.statusCode,
                                response.body()?.message
                            )
                        }
                    } else {
                        val errorModel = getApiError(response.errorBody()?.string())
                        getView()?.handleApiError(errorModel.statusCode, errorModel.message)
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Any>>?, t: Throwable?) {
                    getView()?.apiFailure()
                    getView()?.showLoader(false)
                }
            })
    }
}