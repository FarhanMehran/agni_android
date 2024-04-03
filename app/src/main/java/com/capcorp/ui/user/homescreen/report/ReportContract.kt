package com.capcorp.ui.user.homescreen.report

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView

class ReportContract {
    interface View : BaseView {
        fun onReportSuccess()
    }

    interface Presenter : BasePresenter<View> {
        fun reportOrder(accessToken: String, opposition_id: String, reason: String, orderId: String)
    }
}