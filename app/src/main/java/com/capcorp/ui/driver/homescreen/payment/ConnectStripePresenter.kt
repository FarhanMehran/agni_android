package com.capcorp.ui.driver.homescreen.payment

import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConnectStripePresenter : BasePresenterImpl<ConectStripContract.View>(),
    ConectStripContract.Presenter {
    override fun onConnectStripeApi(accessToken: String, code: String?, stripeConnectId: String?) {
        getView()?.showLoader(true)
        if (stripeConnectId != null) {
            if (code != null) {
                RestClient.get().connectStripe(accessToken, code,stripeConnectId)
                    .enqueue(object : Callback<ApiResponse<Any>> {
                        override fun onFailure(call: Call<ApiResponse<Any>>?, t: Throwable?) {
                            getView()?.showLoader(false)
                            getView()?.apiFailure()
                        }

                        override fun onResponse(
                            call: Call<ApiResponse<Any>>?,
                            response: Response<ApiResponse<Any>>?
                        ) {
                            getView()?.showLoader(false)
                            if (response?.isSuccessful == true) {
                                if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                                    getView()?.onConnectStripeSuccess()
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
    }
}