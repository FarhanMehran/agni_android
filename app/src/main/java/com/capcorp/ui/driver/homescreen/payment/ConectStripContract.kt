package com.capcorp.ui.driver.homescreen.payment

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView

class ConectStripContract {
    interface View : BaseView {
        fun onConnectStripeSuccess()
    }

    interface Presenter : BasePresenter<View> {
        fun onConnectStripeApi(accessToken: String, code: String?, stripeConnectId: String?)
    }
}