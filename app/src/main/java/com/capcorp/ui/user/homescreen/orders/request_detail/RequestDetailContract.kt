package com.capcorp.ui.user.homescreen.orders.request_detail

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.DataApplyCoupon
import com.capcorp.webservice.models.DataRemoveCoupon
import com.capcorp.webservice.models.H2dFeeResponse
import com.capcorp.webservice.models.orders.OrderListing
import com.capcorp.webservice.models.request_model.AcceptOrderRequest


class RequestDetailContract {

    interface View : BaseView {
        fun acceptRequestSuccess()
        fun orderDetailSuccess(data: OrderListing)
        fun applyCouponSuccess(body: DataApplyCoupon)
        fun removeCouponSuccess(body: DataRemoveCoupon?)
        fun getH2DFeeSucess(h2dFee: H2dFeeResponse?)
    }

    interface Presenter : BasePresenter<View> {
        fun acceptRequest(authorization: String, acceptOrderRequest: AcceptOrderRequest)
        fun getOrderDetail(authorization: String, orderId: String)
        fun applyCoupon(authorization: String, orderId: String, couponCode: String)
        fun removeCoupon(authorization: String, orderId: String, couponCode: String)
        fun getH2dFee(auth_key: String)

    }
}