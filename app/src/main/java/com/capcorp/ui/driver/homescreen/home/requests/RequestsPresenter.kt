package com.capcorp.ui.driver.homescreen.home.requests

import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.SignupModel
import com.capcorp.webservice.models.orders.Order
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestsPresenter : BasePresenterImpl<RequestContract.View>(), RequestContract.Presenter {

    override fun makeOffersAndAcceptOrderApiCall(
        accessToken: String?,
        orderId: String?,
        price: String?,
        driverAction: String?,
        position: Int?,
        latitude: Double,
        longitude: Double,
        deliverDate: String,
        driverCardId: String,
        shippingCharge: Double
    ) {
        RestClient.get().makeOffersAndAcceptOrder(
            accessToken,
            orderId,
            price,
            driverAction,
            latitude,
            longitude,
            deliverDate,
            driverCardId,
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
                            position?.let { getView()?.acceptOrderApiSuccess(it) }
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

    override fun getRequestsApiCall(
        accessToken: String?,
        limit: Int?,
        skip: Int?,
        orderType: String?,
        pickCountry: java.util.ArrayList<Double>?,
        dropDownCountry: java.util.ArrayList<Double>?,
        location: ArrayList<Double>
    ) {
        RestClient.get().getRequests(
            authorization = accessToken,
            limit = limit,
            skip = skip,
            orderType = orderType,
            dropDownCountry = dropDownCountry,
            pickUpCountry = pickCountry,
            location = location
        )
            .enqueue(object : Callback<ApiResponse<Order>> {
                override fun onFailure(call: Call<ApiResponse<Order>>?, t: Throwable?) {
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<Order>>?,
                    response: Response<ApiResponse<Order>>?
                ) {
                    if (response?.isSuccessful == true) {
                        if (response.body()?.statusCode?.equals(STATUS_CODE_SUCCESS)!!) {
                            response.body()?.data?.orderListing?.let { getView()?.onApiSuccess(it) }
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

    override fun filterApi(accessToken: String?, hashMap: HashMap<String, Any>) {
        getView()?.showLoader(true)
        RestClient.get().applyFilter(accessToken, hashMap)
            .enqueue(object : Callback<ApiResponse<SignupModel>> {
                override fun onFailure(call: Call<ApiResponse<SignupModel>>?, t: Throwable?) {
                    getView()?.apiFailure()
                    getView()?.showLoader(false)
                }

                override fun onResponse(
                    call: Call<ApiResponse<SignupModel>>?,
                    response: Response<ApiResponse<SignupModel>>?
                ) {
                    getView()?.showLoader(false)
                    if (response?.isSuccessful == true) {
                        if (response.body()?.statusCode?.equals(STATUS_CODE_SUCCESS)!!) {
                            getView()?.onFilterSuccess(response.body()?.data!!)
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