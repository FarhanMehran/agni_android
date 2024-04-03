package com.capcorp.ui.user_signup.verification_code

import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.SendOtpResponse
import com.capcorp.webservice.models.SignupModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PhoneVerficationPresenter : BasePresenterImpl<PhoneVerificationContract.View>(),
    PhoneVerificationContract.Presenter {

    override fun sendOtpApiCall(phoneNo: String, oldNumber: String?, asSend: String) {
        getView()?.showLoader(true)
        RestClient.get().sendOtp(
            oldfullNumber = oldNumber,
            phoneNumber = phoneNo,
            asSend = asSend
        ).enqueue(object : Callback<ApiResponse<SendOtpResponse>> {
            override fun onResponse(
                call: Call<ApiResponse<SendOtpResponse>>?,
                response: Response<ApiResponse<SendOtpResponse>>?
            ) {
                getView()?.showLoader(false)
                if (response!!.isSuccessful) {
                    if (response.body()?.statusCode == 200) {
                        getView()?.otpSuccess()
                    } else {
                        getView()?.handleApiError(
                            response.body()?.statusCode,
                            "please try again later"
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

    override fun verifyApiCall(phoneNo: String, otp: String) {
        getView()?.showLoader(true)
        RestClient.get().verifyOtp(phoneNo, otp).enqueue(object : Callback<ApiResponse<Any>> {
            override fun onResponse(
                call: Call<ApiResponse<Any>>?,
                response: Response<ApiResponse<Any>>?
            ) {
                getView()?.showLoader(false)
                if (response!!.isSuccessful) {
                    if (response.body()?.statusCode == 200) {
                        getView()?.verifyOtpSuccess()
                    } else {
                        getView()?.handleApiError(
                            response.body()?.statusCode,
                            "Enter valid verification code"
                        )
                    }
                } else {
                    val errorModel = getApiError(response.errorBody()?.string())
                    getView()?.handleApiError(
                        errorModel.statusCode,
                        "Enter valid verification code"
                    )

                }
            }

            override fun onFailure(call: Call<ApiResponse<Any>>?, t: Throwable?) {
                getView()?.apiFailure()
                getView()?.showLoader(false)
            }
        })
    }

    override fun verifyOtpInUpdateCase(
        phoneNo: String,
        otp: String,
        oldNumber: String,
        countryISO: String
    ) {
        getView()?.showLoader(true)
        RestClient.get().verifyOtpInUpdate(phoneNo, otp, oldNumber, countryISO, "phoneUpdate")
            .enqueue(object : Callback<ApiResponse<Any>> {
                override fun onResponse(
                    call: Call<ApiResponse<Any>>?,
                    response: Response<ApiResponse<Any>>?
                ) {
                    getView()?.showLoader(false)
                    if (response!!.isSuccessful) {
                        if (response.body()?.statusCode == 200) {
                            getView()?.verifyOtpSuccess()
                        } else {
                            getView()?.handleApiError(
                                response.body()?.statusCode,
                                "Enter valid verification code"
                            )
                        }
                    } else {
                        val errorModel = getApiError(response.errorBody()?.string())
                        getView()?.handleApiError(
                            errorModel.statusCode,
                            "Enter valid verification code"
                        )

                    }
                }

                override fun onFailure(call: Call<ApiResponse<Any>>?, t: Throwable?) {
                    getView()?.apiFailure()
                    getView()?.showLoader(false)
                }
            })
    }


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
                        getView()?.apiSuccess(response.body()?.data)
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