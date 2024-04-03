package com.capcorp.ui.user.homescreen.orders.requested

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.orders.OrderListing
import com.google.gson.JsonObject

class RequestedContract {
    interface View : BaseView {
        fun apiOrderListing(data: List<OrderListing>)
        fun cancelSucess()
        fun deleteSuccess()
        fun republishOrderSuccess()
        fun apiOrderListingResponse(data: List<OrderListing>)
    }

    interface Presenter : BasePresenter<View> {
        fun orderListing(acessToken: String, orderType: String, skip: String, pageNo: String)
        fun cancelApi(accessToken: String, orderId: JsonObject)
        fun deleteOrder(accessToken: String, orderId: String,travelerAction : String)
        fun republishApi(accessToken: String, orderId: String, formatFromDate: String)
        fun orderListingResponse(
            acessToken: String,
            orderType: String,
            skip: String,
            pageNo: String
        )
    }
}