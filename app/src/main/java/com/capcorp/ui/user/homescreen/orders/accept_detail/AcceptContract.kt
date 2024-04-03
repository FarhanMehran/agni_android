package com.capcorp.ui.user.homescreen.orders.accept_detail

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.driver.driver_request.DriverStatusRequestNoPic
import com.capcorp.webservice.models.driver.driver_request_pic.DriverStatusRequestPic
import com.capcorp.webservice.models.orders.OrderListing
import com.google.gson.JsonObject

interface AcceptContract {
    interface View : BaseView {
        fun cancelSucess()
        fun orderMarkCompleteSucess()
        fun orderDetailSuccess(data: OrderListing)
        fun changeDriverStatus()
        fun changeDriverStatusPic()

    }

    interface Presenter : BasePresenter<View> {
        fun driverStatus(accessToken: String, driverStatusRequest: DriverStatusRequestNoPic)
        fun cancelApi(accessToken: String, orderId: JsonObject)
        fun markComplete(accessToken: String, orderId: String, orderCode: String)
        fun getOrderDetail(authorization: String, orderId: String)
        fun driverStatusPic(accessToken: String, driverStatusRequest: DriverStatusRequestPic)
    }
}