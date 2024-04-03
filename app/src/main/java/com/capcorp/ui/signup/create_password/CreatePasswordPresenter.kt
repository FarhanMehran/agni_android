package com.capcorp.ui.signup.phone_verification

import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.PASSWORD
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.Validations
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.SignUpModelRequest
import com.capcorp.webservice.models.SignupModel
import com.capcorp.webservice.models.VerifyDocumentRequestModel
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Callback
import retrofit2.Response

class CreatePasswordPresenter : BasePresenterImpl<CreatePasswordContract.View>(),
    CreatePasswordContract.Presenter {
    override fun verifyDocuments(
        authorization: String,
        verifyDocumentRequestModel: VerifyDocumentRequestModel
    ) {
        getView()?.showLoader(true)
        RestClient.get().verifyDocument(authorization, verifyDocumentRequestModel)
            .enqueue(object : Callback<ApiResponse<SignupModel>> {
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
                            getView()?.onDocumentUploadedSuccess(response.body()?.data)
                        } else {
                            getView()?.apiFailure()
                        }
                    } else {
                        var jsonObject = JSONObject(response.errorBody()!!.string())
                        var message = jsonObject.getString("message")
                        getView()?.showMessage(message)
                    }
                }
            })
    }

    override fun signupApiCall(map: Map<String, String>) {
        if (isValidationsOk(map)) {
            getView()?.showLoader(true)
            RestClient.get().signUp(map).enqueue(object : Callback<ApiResponse<SignupModel>> {
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
                            getView()?.onSignupSuccess(response.body()?.data)
                        } else {
                            getView()?.apiFailure()
                        }
                    } else {
                        var jsonObject = JSONObject(response.errorBody()!!.string())
                        var message = jsonObject.getString("message")
                        getView()?.showMessage(message)
                    }
                }

            })
        }
    }

    override fun travellerSignup(signUpModelRequest: SignUpModelRequest) {
        getView()?.showLoader(true)
        RestClient.get().travellerSignup(signUpModelRequest)
            .enqueue(object : Callback<ApiResponse<SignupModel>> {
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
                            getView()?.onTravellerSignUpSuccess(response.body()?.data)
                        } else {
                            getView()?.apiFailure()
                        }
                    } else {
                        var jsonObject = JSONObject(response.errorBody()!!.string())
                        var message = jsonObject.getString("message")
                        getView()?.showMessage(message)
                    }
                }

            })

    }


    override fun shopperSignup(
        countryCode: RequestBody,
        fullnumber: RequestBody,
        firstname: RequestBody,
        lastname: RequestBody,
        email: RequestBody,
        lat: RequestBody,
        lng: RequestBody,
        default_lat: RequestBody,
        default_lng: RequestBody,
        password: RequestBody,
        deviceType: RequestBody,
        usertype: RequestBody,
        dateOfBirth: RequestBody,
        deviceToken: RequestBody,
        country: RequestBody,
        languageId: RequestBody,
        countryIso: RequestBody
    ) {
        getView()?.showLoader(true)
        RestClient.get().shopperSignup(
            countryCode,
            fullnumber,
            firstname,
            lastname,
            email,
            lat,
            lng,
            default_lat,
            default_lng,
            password,
            deviceType,
            usertype,
            dateOfBirth,
            deviceToken,
            country,
            languageId,
            countryIso
        ).enqueue(object : Callback<ApiResponse<SignupModel>> {
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
                        getView()?.onTravellerSignUpSuccess(response.body()?.data)
                    } else {
                        getView()?.apiFailure()
                    }
                } else {
                    var jsonObject = JSONObject(response.errorBody()!!.string())
                    var message = jsonObject.getString("message")
                    getView()?.showMessage(message)
                }
            }

        })

    }

    private fun isValidationsOk(map: Map<String, String>): Boolean {
        return if (map[PASSWORD].toString().isEmpty()) {
            getView()?.validationsFailure(Validations.FIELD_EMPTY)
            false
        } else if (map[PASSWORD].toString().length < 6) {
            getView()?.validationsFailure(Validations.FIELD_INVALID)
            false
        } else {
            true
        }
    }
}