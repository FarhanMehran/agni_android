package com.capcorp.ui.signup.enter_password

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.SignupModel

class EnterPasswordContract {

    interface View : BaseView {
        fun onLoginSuccess(data: SignupModel)
        fun sendOtpSuccess()
    }


    interface Presenter : BasePresenter<View> {
        fun loginApiCall(map: Map<String, String>)
        fun sendOtpApiCall(phoneNo: String, asSend: String)
    }
}