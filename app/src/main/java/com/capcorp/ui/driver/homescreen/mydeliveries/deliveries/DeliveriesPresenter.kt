package com.capcorp.ui.driver.homescreen.mydeliveries.deliveries

import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.orders.Order
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeliveriesPresenter : BasePresenterImpl<DeliveriesContract.View>(),
    DeliveriesContract.Presenter {

    override fun makeOffersAndAcceptOrderApiCall(
        accessToken: String?,
        orderId: String?,
        price: String?,
        driverAction: String?,
        latitude: Double,
        longitude: Double,
        deliverDate: String,
        reason: String
    ) {
        RestClient.get().rejectOrder(
            accessToken,
            orderId,
            price,
            driverAction,
            latitude,
            longitude,
            deliverDate,
            reason
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
                            getView()?.rejectOrder()
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


    override fun getMyDeliveries(
        accessToken: String?,
        limit: Int?,
        skip: Int?,
        orderStatus: String?,
        pickUpCountry: String?,
        dropDownCountry: String
    ) {
        RestClient.get()
            .getMyDeliveries(accessToken, limit, skip, orderStatus, pickUpCountry, dropDownCountry)
            .enqueue(object : Callback<ApiResponse<Order>> {
                override fun onFailure(call: Call<ApiResponse<Order>>?, t: Throwable?) {
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<Order>>?,
                    response: Response<ApiResponse<Order>>?
                ) {
                    if (response?.isSuccessful!!) {
                        if (response.body()?.statusCode?.equals(STATUS_CODE_SUCCESS)!!) {
                            getView()?.onDeliveriesApiSuccess(response.body()?.data?.orderListing)
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