package com.capcorp.ui.driver.homescreen.home.makeoffer

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView

class MakeAnOfferContract {

    interface View : BaseView {
        fun makeOfferApiSuccess()
    }

    interface Presenter : BasePresenter<View> {
        fun makeOffersAndAcceptOrderApiCall(
            accessToken: String?,
            orderId: String?,
            price: String?,
            driverAction: String?,
            latitude: Double?,
            longitude: Double?,
            deliveryDate: String,
            driverCardId: String,
            shippingCharge: Double
        )
    }
}