package com.capcorp.ui.driver.homescreen.home.grocery_request_detail

import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestPresenter : BasePresenterImpl<RequestContract.View>(), RequestContract.Presenter {
    override fun acceptApi(
        authentication: String,
        orderId: String,
        payment: String,
        action: String,
        latitude: Double,
        longitude: Double,
        deliverDate: String,
        cardId: String,
        shippingCharge: Double
    ) {
        RestClient.get().makeOffersAndAcceptOrder(
            authentication,
            orderId,
            payment,
            action,
            latitude,
            longitude,
            deliverDate,
            cardId,
            shippingCharge
        )
            .enqueue(object : Callback<ApiResponse<Any>> {
                override fun onFailure(call: Call<ApiResponse<Any>>?, t: Throwable?) {
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<Any>>?,
                    response: Response<ApiResponse<Any>>?
                ) {
                    if (response?.isSuccessful == true) {
                        if (response.body()?.statusCode?.equals(STATUS_CODE_SUCCESS)!!) {
                            getView()?.acceptSuccess()
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
