package com.capcorp.ui.signup.resetpassword

import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.getApiError
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.PojoSuccess
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordPresenter : BasePresenterImpl<ResetPasswordContract.View>(),
    ResetPasswordContract.Presenter {
    override fun hitApi(otp: String, phoneNo: String, newPassword: String) {
        getView()?.showLoader(true)
        RestClient.get().apiChangePassword(otp, phoneNo, newPassword)
            .enqueue(object : Callback<PojoSuccess> {
                override fun onResponse(call: Call<PojoSuccess>, response: Response<PojoSuccess>) {
                    getView()?.showLoader(false)
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == 200) {
                            response.body()
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

                override fun onFailure(call: Call<PojoSuccess>?, t: Throwable?) {
                    getView()?.showLoader(false)
                    getView()?.apiFailure()
                }
            })
    }
}
