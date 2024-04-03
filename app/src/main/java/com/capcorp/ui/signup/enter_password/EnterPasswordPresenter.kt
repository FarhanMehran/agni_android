package com.capcorp.ui.signup.enter_password

import com.capcorp.utils.*
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.SendOtpResponse
import com.capcorp.webservice.models.SignupModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EnterPasswordPresenter : BasePresenterImpl<EnterPasswordContract.View>(),
    EnterPasswordContract.Presenter {

    override fun loginApiCall(map: Map<String, String>) {
        if (isValidationsOk(map)) {
            getView()?.showLoader(true)
            RestClient.get().login(map).enqueue(object : Callback<ApiResponse<SignupModel>> {
                override fun onFailure(
                    call: retrofit2.Call<ApiResponse<SignupModel>>?,
                    t: Throwable?
                ) {
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
                            response.body()?.data?.let { getView()?.onLoginSuccess(it) }
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


    override fun sendOtpApiCall(phoneNo: String, asSend: String) {
        getView()?.showLoader(true)
        RestClient.get().sendOtp(
            phoneNumber = phoneNo,
            oldfullNumber = null,
            asSend = asSend
        ).enqueue(object : Callback<ApiResponse<SendOtpResponse>> {
            override fun onResponse(
                call: Call<ApiResponse<SendOtpResponse>>?,
                response: Response<ApiResponse<SendOtpResponse>>?
            ) {
                getView()?.showLoader(false)
                if (response!!.isSuccessful) {
                    if (response.body()?.statusCode == 200) {
                        getView()?.sendOtpSuccess()
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

            override fun onFailure(call: Call<ApiResponse<SendOtpResponse>>?, t: Throwable?) {
                getView()?.apiFailure()
                getView()?.showLoader(false)
            }
        })
    }


    private fun isValidationsOk(map: Map<String, String>): Boolean {
        return when {
            map[PASSWORD].toString().isEmpty() -> {
                getView()?.validationsFailure(Validations.FIELD_EMPTY)
                false
            }
            map[PASSWORD].toString().length < 6 -> {
                getView()?.validationsFailure(Validations.FIELD_INVALID)
                false
            }
            else -> true
        }
    }
}