package com.capcorp.ui.driver.homescreen.home.grocery_request_detail

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView


interface RequestContract {
    interface View : BaseView {
        fun acceptSuccess()
    }

    interface Presenter : BasePresenter<View> {
        fun acceptApi(
            authentication: String,
            orderId: String,
            payment: String,
            action: String,
            latitude: Double,
            longitude: Double,
            deliverDate: String,
            cardId: String,
            shippingCharge: Double
        )
    }
}