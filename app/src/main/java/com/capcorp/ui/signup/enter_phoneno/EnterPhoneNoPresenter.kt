package com.capcorp.ui.signup.phone_verification

import android.util.Patterns
import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.Validations
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.SendOtpResponse
import com.capcorp.webservice.models.SignupModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EnterPhoneNoPresenter : BasePresenterImpl<EnterPhoneNoContract.View>(),
    EnterPhoneNoContract.Presenter {

    override fun emailVerificationApiCall(email: String) {
        getView()?.showLoader(true)
        RestClient.get().checkEmailExists(email)
            .enqueue(object : Callback<ApiResponse<SignupModel>> {
                override fun onResponse(
                    call: Call<ApiResponse<SignupModel>>?,
                    response: Response<ApiResponse<SignupModel>>?
                ) {
                    getView()?.showLoader(false)
                    if (response!!.isSuccessful) {
                        if (response.body()?.statusCode == 200) {
                            getView()?.apiEmailSuccess(
                                response.body()?.data?.userExists
                                    ?: false, response.body()?.data!!
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

                override fun onFailure(call: Call<ApiResponse<SignupModel>>?, t: Throwable?) {
                    getView()?.apiFailure()
                    getView()?.showLoader(false)
                }
            })
    }

    override fun phoneVerificationApiCall(phoneNo: String, type: String, asCheck: String) {
        if (isValidationsOk(phoneNo)) {
            getView()?.showLoader(true)
            RestClient.get().checkPhoneNumberExist(phoneNo, type, asCheck)
                .enqueue(object : Callback<ApiResponse<SignupModel>> {
                    override fun onResponse(
                        call: Call<ApiResponse<SignupModel>>?,
                        response: Response<ApiResponse<SignupModel>>?
                    ) {
                        getView()?.showLoader(false)
                        if (response!!.isSuccessful) {
                            if (response.body()?.statusCode == 200) {
                                getView()?.apiSuccess(
                                    response.body()?.data?.userExists ?: true,
                                    response.body()?.data!!
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

                    override fun onFailure(call: Call<ApiResponse<SignupModel>>?, t: Throwable?) {
                        getView()?.apiFailure()
                        getView()?.showLoader(false)
                    }
                })
        }
    }


    override fun sendOtpApiCall(phoneNo: String, oldNumber: String?, asSend: String) {
        if (isValidationsOk(phoneNo)) {
            getView()?.showLoader(true)
            RestClient.get().sendOtp(
                phoneNumber = phoneNo,
                oldfullNumber = oldNumber, asSend = asSend
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
    }

    override fun socialLogin(map: Map<String, String>) {

    }


    private fun isValidationsOk(phoneNo: String): Boolean {
        return if (phoneNo.isEmpty()) {
            getView()?.validationsFailure(Validations.FIELD_EMPTY)
            false
        } else if (!Patterns.PHONE.matcher(phoneNo).matches()) {
            getView()?.validationsFailure(Validations.FIELD_INVALID)
            false
        } else {
            true
        }
    }

}