package com.capcorp.ui.user.homescreen.account.activity

import com.capcorp.ui.driver.stripe.ConnectStripeOnBoarding
import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView

class PayoutContract {


    interface View : BaseView {
        fun countryAPISuccess(data: Array<String>?)
        fun onConnectStripeOnBoardingSuccess(stripeData : ConnectStripeOnBoarding.Data)
    }

    interface Presenter : BasePresenter<View> {
        fun getCountryAPICall()
        fun onConnectStripeOnBoardingApi(accessToken: String, code: String)
    }
}