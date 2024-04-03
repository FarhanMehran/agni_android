package com.capcorp.ui.user.homescreen.report

import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportPresenter : BasePresenterImpl<ReportContract.View>(), ReportContract.Presenter {
    override fun reportOrder(
        accessToken: String,
        opposition_id: String,
        reason: String,
        orderId: String
    ) {
        getView()?.showLoader(true)
        RestClient.get().reportOrder(accessToken, opposition_id, reason, orderId)
            .enqueue(object : Callback<ApiResponse<Any>> {
                override fun onFailure(call: Call<ApiResponse<Any>>?, t: Throwable?) {
                    getView()?.apiFailure()
                    getView()?.showLoader(false)
                }

                override fun onResponse(
                    call: Call<ApiResponse<Any>>?,
                    response: Response<ApiResponse<Any>>
                ) {
                    getView()?.showLoader(false)
                    if (response.isSuccessful == true) {
                        if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                            getView()?.onReportSuccess()
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