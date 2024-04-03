package com.capcorp.ui.user.homescreen.account

import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.FaqModel
import com.capcorp.webservice.models.ReasonModel
import com.capcorp.webservice.models.SignupModel
import com.capcorp.webservice.models.SupportRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AccountPresenter : BasePresenterImpl<AccountContract.View>(), AccountContract.Presenter {
    override fun reasonApi(acessToken: String, type: String) {
        getView()?.showLoader(true)
        RestClient.get().getReason(acessToken, type)
            .enqueue(object : Callback<ApiResponse<ReasonModel>> {
                override fun onResponse(
                    call: Call<ApiResponse<ReasonModel>>,
                    response: Response<ApiResponse<ReasonModel>>
                ) {
                    getView()?.showLoader(false)
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == 200) {
                            getView()?.reasonSuccess(response.body()?.data?.reasonListing!!)
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

                override fun onFailure(call: Call<ApiResponse<ReasonModel>>?, t: Throwable?) {
                    getView()?.apiFailure()
                    getView()?.showLoader(false)
                }
            })
    }

    override fun languageChangeApi(acessToken: String, type: Int) {
        getView()?.showLoader(true)
        RestClient.get().languageChange(acessToken, type)
            .enqueue(object : Callback<ApiResponse<Any>> {
                override fun onResponse(
                    call: Call<ApiResponse<Any>>,
                    response: Response<ApiResponse<Any>>
                ) {
                    getView()?.showLoader(false)
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == 200) {
                            getView()?.languageSuccess()
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

                override fun onFailure(call: Call<ApiResponse<Any>>?, t: Throwable?) {
                    getView()?.apiFailure()
                    getView()?.showLoader(false)
                }
            })
    }


    override fun blockPush(acessToken: String, updateStatus: String) {
        getView()?.showLoader(true)
        RestClient.get().blockPush(acessToken, updateStatus)
            .enqueue(object : Callback<ApiResponse<SignupModel>> {
                override fun onResponse(
                    call: Call<ApiResponse<SignupModel>>,
                    response: Response<ApiResponse<SignupModel>>
                ) {
                    getView()?.showLoader(false)
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == 200) {
                            getView()?.blockPush(response.body()?.data!!)
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

    override fun supportRequest(acessToken: String, supportRequest: SupportRequest) {
        getView()?.showLoader(true)
        RestClient.get().support(acessToken, supportRequest)
            .enqueue(object : Callback<ApiResponse<Any>> {
                override fun onResponse(
                    call: Call<ApiResponse<Any>>,
                    response: Response<ApiResponse<Any>>
                ) {
                    getView()?.showLoader(false)
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == 200) {
                            getView()?.supportSuccess()
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

                override fun onFailure(call: Call<ApiResponse<Any>>?, t: Throwable?) {
                    getView()?.apiFailure()
                    getView()?.showLoader(false)
                }
            })
    }

    override fun faq() {
        getView()?.showLoader(true)
        RestClient.get().faq().enqueue(object : Callback<ApiResponse<FaqModel>> {
            override fun onResponse(
                call: Call<ApiResponse<FaqModel>>,
                response: Response<ApiResponse<FaqModel>>
            ) {
                getView()?.showLoader(false)
                if (response.isSuccessful) {
                    if (response.body()?.statusCode == 200) {
                        getView()?.onFaqSuccess(response.body()?.data?.result!!)
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

            override fun onFailure(call: Call<ApiResponse<FaqModel>>?, t: Throwable?) {
                getView()?.apiFailure()
                getView()?.showLoader(false)
            }
        })
    }

    override fun switchUser(acessToken: String, type: String) {
        getView()?.showLoader(true)
        RestClient.get().switchToDriver(acessToken, type)
            .enqueue(object : Callback<ApiResponse<SignupModel>> {
                override fun onResponse(
                    call: Call<ApiResponse<SignupModel>>,
                    response: Response<ApiResponse<SignupModel>>
                ) {
                    getView()?.showLoader(false)
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == 200) {
                            getView()?.switchUserSuccess(response.body()?.data!!)
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

    override fun logout(acessToken: String) {
        getView()?.showLoader(true)
        RestClient.get().logout(acessToken).enqueue(object : Callback<ApiResponse<Any>> {
            override fun onResponse(
                call: Call<ApiResponse<Any>>,
                response: Response<ApiResponse<Any>>
            ) {
                getView()?.showLoader(false)
                if (response.isSuccessful) {
                    if (response.body()?.statusCode == 200) {
                        getView()?.logoutSuccess()
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

            override fun onFailure(call: Call<ApiResponse<Any>>?, t: Throwable?) {
                getView()?.apiFailure()
                getView()?.showLoader(false)
            }
        })
    }

    override fun editProfileApiCall(authToken: String, map: Map<String, String>) {
        getView()?.showLoader(true)
        RestClient.get().editProfile(authToken, map)
            .enqueue(object : Callback<ApiResponse<SignupModel>> {
                override fun onResponse(
                    call: Call<ApiResponse<SignupModel>>,
                    response: Response<ApiResponse<SignupModel>>
                ) {
                    getView()?.showLoader(false)
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == 200) {
                            getView()?.onEditProfileApiSuccess(response.body()?.data)
                        } else {
                            getView()?.handleApiError(
                                response.body()?.statusCode,
                                response.body()?.message
                            )
                        }
                    } else {


                        val errorModel = getApiError(response.errorBody()?.string())
                        getView()?.handleApiError(500, errorModel.message)
                    }
                }

                override fun onFailure(call: Call<ApiResponse<SignupModel>>?, t: Throwable?) {
                    getView()?.apiFailure()
                    getView()?.showLoader(false)
                }
            })
    }

}