package com.capcorp.ui.user.homescreen.orders.completed_job_details

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView


class CompletedDetailContract {

    interface View : BaseView {
        fun ratingSuccess()
    }

    interface Presenter : BasePresenter<View> {
        fun ratingRequest(
            authorization: String,
            rating: Double,
            orderId: String,
            description: String
        )
    }
}