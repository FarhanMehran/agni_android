package com.capcorp.ui.driver.homescreen.mydeliveries.deliveries

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.orders.OrderListing

class DeliveriesContract {

    interface View : BaseView {
        fun onDeliveriesApiSuccess(orderList: List<OrderListing>?)
        fun rejectOrder()


    }

    interface Presenter : BasePresenter<View> {
        fun getMyDeliveries(
            accessToken: String?,
            limit: Int?,
            skip: Int?,
            orderStatus: String?,
            pickUpCountry: String?,
            dropDownCountry: String
        )

        fun makeOffersAndAcceptOrderApiCall(
            accessToken: String?,
            orderId: String?,
            price: String?,
            driverAction: String?,
            latitude: Double,
            longitude: Double,
            deliverDate: String,
            reason: String
        )

    }
}