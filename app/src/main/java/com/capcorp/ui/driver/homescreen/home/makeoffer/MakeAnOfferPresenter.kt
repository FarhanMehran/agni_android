package com.capcorp.ui.driver.homescreen.home.makeoffer

import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MakeAnOfferPresenter : BasePresenterImpl<MakeAnOfferContract.View>(),
    MakeAnOfferContract.Presenter {

    override fun makeOffersAndAcceptOrderApiCall(
        accessToken: String?,
        orderId: String?,
        price: String?,
        driverAction: String?,
        latitude: Double?,
        longitude: Double?,
        deliveryDate: String,
        driverCardId: String,
        shippingCharge: Double
    ) {
        getView()?.showLoader(true)
        RestClient.get().makeOffersAndAcceptOrder(
            accessToken,
            orderId,
            price,
            driverAction,
            latitude,
            longitude,
            deliveryDate,
            driverCardId,
            shippingCharge
        )
            .enqueue(object : Callback<ApiResponse<Any>> {
                override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable?) {
                    getView()?.showLoader(false)
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<Any>>,
                    response: Response<ApiResponse<Any>>
                ) {
                    getView()?.showLoader(false)
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode?.equals(STATUS_CODE_SUCCESS)!!) {
                            getView()?.makeOfferApiSuccess()
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

            })
    }


}