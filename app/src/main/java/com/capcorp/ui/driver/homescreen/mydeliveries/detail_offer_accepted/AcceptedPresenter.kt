package com.capcorp.ui.driver.homescreen.mydeliveries.detail_offer_accepted

import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.CancelOrderRequest
import com.capcorp.webservice.models.driver.driver_request.DriverStatusRequestNoPic
import com.capcorp.webservice.models.driver.driver_request_pic.DriverStatusRequestPic
import com.capcorp.webservice.models.orders.OrderListing
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AcceptedPresenter : BasePresenterImpl<AcceptedContract.View>(), AcceptedContract.Presenter {
    override fun cancelOrderBooking(accessToken: String, cancelOrderRequest: CancelOrderRequest) {
        RestClient.get().cancelOrderBooking(accessToken, cancelOrderRequest)
            .enqueue(object : Callback<ApiResponse<Any>> {
                override fun onFailure(call: Call<ApiResponse<Any>>?, t: Throwable?) {
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<Any>>?,
                    response: Response<ApiResponse<Any>>?
                ) {
                    if (response?.isSuccessful!!) {
                        if (response.body()?.statusCode?.equals(STATUS_CODE_SUCCESS)!!) {
                            getView()?.cancelOrder()
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

    override fun cancelOrder(accessToken: String, orderID: JsonObject) {
        RestClient.get().cancelOrder(accessToken, orderID)
            .enqueue(object : Callback<ApiResponse<Any>> {
                override fun onFailure(call: Call<ApiResponse<Any>>?, t: Throwable?) {
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<Any>>?,
                    response: Response<ApiResponse<Any>>?
                ) {
                    if (response?.isSuccessful!!) {
                        if (response.body()?.statusCode?.equals(STATUS_CODE_SUCCESS)!!) {
                            getView()?.cancelOrder()
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

    override fun driverStatus(accessToken: String, driverStatusRequest: DriverStatusRequestNoPic) {
        RestClient.get().driverStatus(accessToken, driverStatusRequest)
            .enqueue(object : Callback<ApiResponse<Any>> {
                override fun onFailure(call: Call<ApiResponse<Any>>?, t: Throwable?) {
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<Any>>?,
                    response: Response<ApiResponse<Any>>?
                ) {
                    if (response?.isSuccessful!!) {
                        if (response.body()?.statusCode?.equals(STATUS_CODE_SUCCESS)!!) {
                            getView()?.changeDriverStatus()
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

    override fun driverStatusPic(accessToken: String, driverStatusRequest: DriverStatusRequestPic) {
        RestClient.get().driverStatusWithPic(accessToken, driverStatusRequest)
            .enqueue(object : Callback<ApiResponse<Any>> {
                override fun onFailure(call: Call<ApiResponse<Any>>?, t: Throwable?) {
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<Any>>?,
                    response: Response<ApiResponse<Any>>?
                ) {
                    if (response?.isSuccessful!!) {
                        if (response.body()?.statusCode?.equals(STATUS_CODE_SUCCESS)!!) {
                            getView()?.changeDriverStatusPic()
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

    override fun getOrderDetail(authorization: String, orderId: String) {
        RestClient.get().getOrderDetail(authorization, orderId)
            .enqueue(object : Callback<ApiResponse<OrderListing>> {
                override fun onFailure(call: Call<ApiResponse<OrderListing>>?, t: Throwable?) {
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<OrderListing>>,
                    response: Response<ApiResponse<OrderListing>>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                            response.body()?.data?.let { getView()?.orderDetailSuccess(it) }
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