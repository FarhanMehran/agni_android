package com.capcorp.ui.user.homescreen.orders.completed_job_details

import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.getApiError
import com.capcorp.webservice.ApiResponse
import com.capcorp.webservice.RestClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompletedDetailPresenter : BasePresenterImpl<CompletedDetailContract.View>(),
    CompletedDetailContract.Presenter {
    override fun ratingRequest(
        authorization: String,
       /* opppsitionId: String,*/
        rating: Double,
        orderId: String,
        /*type: String,*/
        description: String
    ) {
        RestClient.get()
            .ratingrequest(authorization, rating, orderId, description)
            .enqueue(object : Callback<ApiResponse<Any>> {
                override fun onFailure(call: Call<ApiResponse<Any>>?, t: Throwable?) {
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ApiResponse<Any>>,
                    response: Response<ApiResponse<Any>>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                            getView()?.ratingSuccess()
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
