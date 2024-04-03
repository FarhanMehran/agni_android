package com.capcorp.ui.user.homescreen.orders.add_receiver

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.request_model.AddReciverRequest

interface ReceiverContract {
    interface View : BaseView {
        fun apiSuccess()
    }

    interface Presenter : BasePresenter<View> {
        fun apiAddReciver(accessToken: String, addReceiverDetail: AddReciverRequest)
    }
}