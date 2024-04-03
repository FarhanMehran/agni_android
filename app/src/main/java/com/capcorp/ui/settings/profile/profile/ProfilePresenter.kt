package com.capcorp.ui.settings.profile.profile

import android.util.Patterns
import com.capcorp.utils.*
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.SignupModel
import com.capcorp.webservice.models.image_upload.ImageUploadModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfilePresenter : BasePresenterImpl<ProfileContract.View>(), ProfileContract.Presenter {

    override fun editProfileApiCall(authToken: String, map: Map<String, String>) {
        getView()?.showLoader(true)
        RestClient.get().editProfile(authToken, map)
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
                        if (response.body()?.statusCode == 200) {
                            getView()?.onEditProfileApiSuccess(response.body()?.data)
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

    override fun imageUploadCall(file: File) {

        var fileOf = "User".toRequestBody("text/plain".toMediaTypeOrNull())

        var imgFile = MultipartBody.Part.createFormData(
            "file", file.name, file
                .asRequestBody("image/*".toMediaTypeOrNull())
        )

        getView()?.showLoader(true)
        RestClient.get().uploadImage(fileOf, imgFile)
            .enqueue(object : Callback<ApiResponse<ImageUploadModel>> {
                override fun onFailure(call: Call<ApiResponse<ImageUploadModel>>?, t: Throwable?) {
                    getView()?.apiFailure()
                    getView()?.showLoader(false)
                }

                override fun onResponse(
                    call: Call<ApiResponse<ImageUploadModel>>?,
                    response: Response<ApiResponse<ImageUploadModel>>?
                ) {
                    getView()?.showLoader(false)
                    if (response?.isSuccessful == true) {
                        if (response.body()?.statusCode == 200) {
                            getView()?.onUploadImageSuccess(response.body()?.data!!)
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

    override fun phoneVerificationApiCall(phoneNo: String, type: String, asCheck: String) {

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
                            getView()?.apiNumberSuccess(
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

    fun checkValidations(map: Map<String, String>) {
        if (map.containsKey(FIRST_NAME) && map.getValue(FIRST_NAME).isEmpty()) {
            getView()?.validationsFailure(Validations.FIRST_NAME_EMPTY)
        } else if (map.containsKey(LAST_NAME) && map.getValue(LAST_NAME).isEmpty()) {
            getView()?.validationsFailure(Validations.LAST_NAME_EMPTY)
        } else if (map.containsKey(EMAIL) && map.getValue(EMAIL).isEmpty()) {
            getView()?.validationsFailure(Validations.EMAIL_EMPTY)
        } else if (map.containsKey(PHONE_NUMBER) && map.getValue(PHONE_NUMBER).isEmpty()) {
            getView()?.validationsFailure(Validations.NUMBER_EMPTY)
        } else if (map.containsKey(PHONE_NUMBER) && map.getValue(PHONE_NUMBER).length < 10) {
            getView()?.validationsFailure(Validations.NUMBER_INVALID)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(map.getValue(EMAIL)).matches()) {
            getView()?.validationsFailure(Validations.EMAIL_INVALID)
        } else {
            getView()?.onValidationsSuccess(map)
        }
    }
}