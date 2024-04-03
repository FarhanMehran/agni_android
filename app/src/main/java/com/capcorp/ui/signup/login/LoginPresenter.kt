package com.capcorp.ui.signup.login

import android.util.Patterns
import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.Validations
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.SignupModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPresenter : BasePresenterImpl<LoginContract.View>(), LoginContract.Presenter {

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
                        if (response?.isSuccessful == true) {
                            if (response.body()?.statusCode == 200) {
                                getView()?.apiSuccess(response.body()?.data)
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

                    override fun onFailure(call: Call<ApiResponse<SignupModel>>?, t: Throwable?) {
                        getView()?.apiFailure()
                        getView()?.showLoader(false)
                    }
                })
        }
    }

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
                                    ?: false
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
                        getView()?.socialLoginApiSuccess(response.body()?.data)
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