package com.capcorp.ui.driver.homescreen.home.requests

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.SignupModel
import com.capcorp.webservice.models.orders.OrderListing

class RequestContract {

    interface View : BaseView {
        fun onApiSuccess(orderList: List<OrderListing>)
        fun acceptOrderApiSuccess(position: Int)
        fun onFilterSuccess(signupModel: SignupModel)
    }

    interface Presenter : BasePresenter<View> {
        fun filterApi(accessToken: String?, hashMap: HashMap<String, Any>)
        fun getRequestsApiCall(
            accessToken: String?,
            limit: Int?,
            skip: Int?,
            orderType: String?,
            pickCountry: ArrayList<Double>?,
            dropDownCountry: ArrayList<Double>?,
            location: ArrayList<Double>
        )

        fun makeOffersAndAcceptOrderApiCall(
            accessToken: String?,
            orderId: String?,
            price: String?,
            driverAction: String?,
            position: Int?,
            latitude: Double,
            longitude: Double,
            deliverDate: String,
            driverCardId: String,
            shippingCharge: Double
        )
    }
}