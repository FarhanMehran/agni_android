package com.capcorp.ui.splash

import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.H2dFeeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashPresenter : BasePresenterImpl<SplashContract.View>(), SplashContract.Presenter {

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

}
