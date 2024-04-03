package com.capcorp.ui.user.homescreen.orders.request_detail

import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.DataApplyCoupon
import com.capcorp.webservice.models.DataRemoveCoupon
import com.capcorp.webservice.models.H2dFeeResponse
import com.capcorp.webservice.models.orders.OrderListing
import com.capcorp.webservice.models.request_model.AcceptOrderRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestDetailPresenter : BasePresenterImpl<RequestDetailContract.View>(),
    RequestDetailContract.Presenter {
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

    override fun acceptRequest(authorization: String, acceptOrderRequest: AcceptOrderRequest) {
        RestClient.get().acceptOffer(authorization, acceptOrderRequest)
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
                            getView()?.acceptRequestSuccess()
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

    override fun applyCoupon(authorization: String, orderId: String, coupon_code: String) {
        RestClient.get().useCoupon(authorization, orderId, coupon_code)
            .enqueue(object : Callback<ApiResponse<ArrayList<DataApplyCoupon>>> {
                override fun onFailure(
                    call: Call<ApiResponse<ArrayList<DataApplyCoupon>>>?,
                    t: Throwable?
                ) {
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<ArrayList<DataApplyCoupon>>>,
                    response: Response<ApiResponse<ArrayList<DataApplyCoupon>>>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                            getView()?.applyCouponSuccess(
                                response.body()!!.data?.get(0) ?: DataApplyCoupon(
                                    0.0,
                                    0.0,
                                    0.0,
                                    "",
                                    0.0
                                )
                            )
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

    override fun getH2dFee(auth_key: String) {
        getView()?.showLoader(true)
        RestClient.get().getH2dFee(auth_key)
            .enqueue(object : Callback<ApiResponse<H2dFeeResponse>> {
                override fun onFailure(call: Call<ApiResponse<H2dFeeResponse>>?, t: Throwable?) {
                    getView()?.showLoader(false)
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<H2dFeeResponse>>?,
                    response: Response<ApiResponse<H2dFeeResponse>?>
                ) {
                    getView()?.showLoader(false)
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                            getView()?.getH2DFeeSucess(response.body()?.data)
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

    override fun removeCoupon(authorization: String, orderId: String, coupon_code: String) {
        RestClient.get().removeCoupon(authorization, orderId, coupon_code)
            .enqueue(object : Callback<ApiResponse<ArrayList<DataRemoveCoupon>>> {
                override fun onFailure(
                    call: Call<ApiResponse<ArrayList<DataRemoveCoupon>>>?,
                    t: Throwable?
                ) {
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<ArrayList<DataRemoveCoupon>>>,
                    response: Response<ApiResponse<ArrayList<DataRemoveCoupon>>>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                            getView()?.removeCouponSuccess(response.body()!!.data?.get(0))
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
