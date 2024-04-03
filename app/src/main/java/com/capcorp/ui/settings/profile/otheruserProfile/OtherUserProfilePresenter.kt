package com.capcorp.ui.settings.profile.otheruserProfile

import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.SignupModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtherUserProfilePresenter : BasePresenterImpl<OtherUserProfileContract.View>(),
    OtherUserProfileContract.Presenter {
    override fun onOtherUserProfile(authToken: String, user_id: String, type: String) {
        getView()?.showLoader(true)
        RestClient.get().getOtherProfile(authToken, user_id)
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
                            getView()?.onOtherUserProfile(response.body()?.data)
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

}