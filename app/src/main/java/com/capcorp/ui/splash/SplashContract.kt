package com.capcorp.ui.splash

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.H2dFeeResponse

interface SplashContract {

    interface View : BaseView {
        fun getH2DFeeSucess(h2dFee: H2dFeeResponse?)
    }

    interface Presenter : BasePresenter<View> {
        fun getH2dFee(auth_key: String)
    }
}