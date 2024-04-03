package com.capcorp.ui.user.homescreen.account.activity

import com.capcorp.ui.driver.stripe.ConnectStripeOnBoarding
import com.capcorp.utils.BasePresenterImpl
import com.capcorp.utils.STATUS_CODE_SUCCESS
import com.capcorp.utils.getApiError
import com.capcorp.webservice.RestClient
import com.capcorp.webservice.models.getcountries.GetCountry
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PayoutsPresenter : BasePresenterImpl<PayoutContract.View>(), PayoutContract.Presenter {

    private var call: Call<GetCountry>? = null

    override fun getCountryAPICall() {
        if (call != null) {
            call?.cancel()
        }
        getView()?.showLoader(true)
        call = RestClient.get().getCountry()
        call?.enqueue(object : Callback<GetCountry> {
            override fun onFailure(call: Call<GetCountry>?, t: Throwable?) {
                getView()?.showLoader(false)
                getView()?.apiFailure()
            }

            override fun onResponse(call: Call<GetCountry>?, response: Response<GetCountry>?) {
                getView()?.showLoader(false)
                if (response?.isSuccessful == true) {
                    if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                        getView()?.countryAPISuccess(response.body()?.data)
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

    override fun onConnectStripeOnBoardingApi(accessToken: String, code: String) {
        getView()?.showLoader(true)
        RestClient.get().connectStripeOnBoarding(accessToken, code)
            .enqueue(object : Callback<ConnectStripeOnBoarding> {
                override fun onFailure(call: Call<ConnectStripeOnBoarding>?, t: Throwable?) {
                    getView()?.showLoader(false)
                    getView()?.apiFailure()
                }

                override fun onResponse(
                    call: Call<ConnectStripeOnBoarding>?,
                    response: Response<ConnectStripeOnBoarding>?
                ) {
                    getView()?.showLoader(false)
                    if (response?.isSuccessful == true) {
                        if (response.body()?.statusCode == STATUS_CODE_SUCCESS) {
                            getView()?.onConnectStripeOnBoardingSuccess(response.body()!!.data)
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