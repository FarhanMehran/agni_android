package com.capcorp.ui.user.homescreen.home.ship_here2dare.location_details.fragment.review

import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.H2dFeeResponse
import com.capcorp.webservice.models.request_model.ShipDataRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewPresenter : BasePresenterImpl<ReviewContract.View>(), ReviewContract.Presenter {
    override fun shipRequestApiCall(
        auth_key: String,
        type: RequestBody,
        description: RequestBody,
        pickUpLocation: RequestBody,
        dropDownLocation: RequestBody,
        pickUpAddress: RequestBody,
        dropDownAddress: RequestBody,
        itemUrl: RequestBody,
        itemPrice: RequestBody,
        itemQuantity: RequestBody,
        payment: RequestBody,
        pickUpdate: RequestBody,
        image: List<MultipartBody.Part>
    ) {

    }

    override fun shipRequestCall(accessToken: String, shipDataRequest: ShipDataRequest) {
        getView()?.showLoader(true)
        RestClient.get().requestShipment(accessToken, shipDataRequest)
            .enqueue(object : Callback<ApiResponse<Any>> {
                override fun onFailure(call: Call<ApiResponse<Any>>?, t: Throwable?) {
                    getView()?.showLoader(false)
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<Any>>?,
                    response: Response<ApiResponse<Any>?>
                ) {
                    getView()?.showLoader(false)
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
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
    /* override fun shipRequestApiCall(auth_key: String, type: RequestBody, description: RequestBody,
                                     pickUpLocation: RequestBody, dropDownLocation: RequestBody, pickUpAddress: RequestBody,
                                     dropDownAddress: RequestBody, itemUrl: RequestBody, itemPrice: RequestBody,
                                     itemQuantity: RequestBody, payment: RequestBody,pickUpdate:RequestBody, image: List<MultipartBody.Part>) {
         getView()?.showLoader(true)
         RestClient.get().requestShip(auth_key,type,description,pickUpLocation,dropDownLocation,
                 pickUpAddress,dropDownAddress,itemUrl,itemPrice,itemQuantity,payment,pickUpdate,image).enqueue(object : Callback<ApiResponse<Any>> {
             override fun onFailure(call: Call<ApiResponse<Any>>?, t: Throwable?) {
                 getView()?.showLoader(false)
                 getView()?.apiFailure()
             }

             override fun onResponse(call: Call<ApiResponse<Any>>?, response: Response<ApiResponse<Any>?>) {
                 getView()?.showLoader(false)
                 if (response.isSuccessful) {
                     if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                         getView()?.apiSuccess()
                     } else {
                         getView()?.handleApiError(response.body()?.statusCode, response.body()?.message)
                     }
                 } else {
                     val errorModel = getApiError(response.errorBody()?.string())
                     getView()?.handleApiError(errorModel.statusCode, errorModel.message)
                 }
             }
         })
     }*/
}
