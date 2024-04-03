package com.capcorp.ui.driver.homescreen.mydeliveries.detail_offer_accepted

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.CancelOrderRequest
import com.capcorp.webservice.models.driver.driver_request.DriverStatusRequestNoPic
import com.capcorp.webservice.models.driver.driver_request_pic.DriverStatusRequestPic
import com.capcorp.webservice.models.orders.OrderListing
import com.google.gson.JsonObject

interface AcceptedContract {
    interface View : BaseView {
        fun changeDriverStatus()
        fun changeDriverStatusPic()
        fun cancelOrder()
        fun rejectOrder()
        fun orderDetailSuccess(data: OrderListing)
    }

    interface Presenter : BasePresenter<View> {
        fun driverStatus(accessToken: String, driverStatusRequest: DriverStatusRequestNoPic)
        fun cancelOrder(accessToken: String, orderID: JsonObject)
        fun cancelOrderBooking(accessToken: String, cancelOrderRequest: CancelOrderRequest)
        fun getOrderDetail(authorization: String, orderId: String)

        fun driverStatusPic(accessToken: String, driverStatusRequest: DriverStatusRequestPic)
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