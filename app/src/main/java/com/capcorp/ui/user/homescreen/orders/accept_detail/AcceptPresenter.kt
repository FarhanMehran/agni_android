package com.capcorp.ui.user.homescreen.orders.accept_detail

import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.driver.driver_request.DriverStatusRequestNoPic
import com.capcorp.webservice.models.driver.driver_request_pic.DriverStatusRequestPic
import com.capcorp.webservice.models.orders.OrderListing
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AcceptPresenter : BasePresenterImpl<AcceptContract.View>(), AcceptContract.Presenter {
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


    override fun markComplete(accessToken: String, orderId: String, orderCode: String) {
        RestClient.get().markCompleteOrder(accessToken, orderId, orderCode)
            .enqueue(object : Callback<ApiResponse<Any>> {
                override fun onFailure(call: Call<ApiResponse<Any>>?, t: Throwable?) {
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<Any>>,
                    response: Response<ApiResponse<Any>>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                            getView()?.orderMarkCompleteSucess()
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

    override fun cancelApi(accessToken: String, orderId: JsonObject) {
        RestClient.get().cancelOrder(accessToken, orderId)
            .enqueue(object : Callback<ApiResponse<Any>> {
                override fun onFailure(call: Call<ApiResponse<Any>>?, t: Throwable?) {
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<Any>>,
                    response: Response<ApiResponse<Any>>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                            getView()?.cancelSucess()
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