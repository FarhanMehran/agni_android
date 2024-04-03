package com.capcorp.ui.signup.phone_verification

import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.SignupModel
import retrofit2.Callback
import retrofit2.Response

class PhoneVerficationPresenter : BasePresenterImpl<PhoneVerificationContract.View>(),
    PhoneVerificationContract.Presenter {

    override fun socialLogin(map: Map<String, String>) {
        getView()?.showLoader(true)
        RestClient.get().socialLogin(map).enqueue(object : Callback<ApiResponse<SignupModel>> {
            override fun onFailure(call: retrofit2.Call<ApiResponse<SignupModel>>?, t: Throwable?) {
                getView()?.showLoader(false)
                getView()?.apiFailure()
            }

            override fun onResponse(
                call: retrofit2.Call<ApiResponse<SignupModel>>?,
                response: Response<ApiResponse<SignupModel>>?
            ) {
                getView()?.showLoader(false)
                if (response!!.isSuccessful) {
                    if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                        getView()?.apiSuccess(response.body()?.data!!)
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

    override fun detachView() {

    }
}