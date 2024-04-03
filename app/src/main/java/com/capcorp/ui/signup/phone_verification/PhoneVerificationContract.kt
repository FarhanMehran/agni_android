package com.capcorp.ui.signup.phone_verification

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.SignupModel

class PhoneVerificationContract {


    interface View : BaseView {
        fun apiSuccess(data: SignupModel)
    }

    interface Presenter : BasePresenter<View> {
        fun socialLogin(map: Map<String, String>)
    }
}