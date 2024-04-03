package com.capcorp.ui.signup.phone_verification

import com.capcorp.ui.user.homescreen.home.HomeContract
import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.KnowMoreResponse
import com.capcorp.webservice.models.home.HomeDataResponse
import com.capcorp.webservice.models.product_information.ProductInformation
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePresenter : BasePresenterImpl<HomeContract.View>(), HomeContract.Presenter {
    override fun getHomeDetails() {
        getView()?.showLoader(true)
        RestClient.get().getHome()
            .enqueue(object : Callback<HomeDataResponse> {
                override fun onFailure(call: Call<HomeDataResponse>?, t: Throwable?) {
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<HomeDataResponse>,
                    response: Response<HomeDataResponse>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                            response.body()!!.data?.let { getView()?.getHomeDetailsSuccess(it) }
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

    override fun getKnowMore() {
        getView()?.showLoader(true)
        RestClient.get().getKnowMore()
            .enqueue(object : Callback<KnowMoreResponse> {
                override fun onFailure(call: Call<KnowMoreResponse>?, t: Throwable?) {
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<KnowMoreResponse>,
                    response: Response<KnowMoreResponse>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                            response.body()!!.data?.let { getView()?.getKnowMoreSuccess(it) }
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


    override fun getProductInformation(token: String, url: RequestBody, content: RequestBody) {
        getView()?.showLoader(true)
        RestClient.get().getProductDetails(token, url, content)
            .enqueue(object : Callback<ApiResponse<ProductInformation>> {
                override fun onFailure(
                    call: Call<ApiResponse<ProductInformation>>?,
                    t: Throwable?
                ) {
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<ProductInformation>>,
                    response: Response<ApiResponse<ProductInformation>>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                            response.body()!!.data?.let { getView()?.getProductInformationSuccess(it) }
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