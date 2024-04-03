package com.capcorp.ui.signup.login

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.SignupModel

class LoginContract {

    interface View : BaseView {
        fun apiSuccess(userInfo: SignupModel?)
        fun socialLoginApiSuccess(userInfo: SignupModel?)
        fun apiEmailSuccess(isPhoneNoExists: Boolean)
    }

    interface Presenter : BasePresenter<View> {
        fun phoneVerificationApiCall(phoneNo: String, type: String, asCheck: String)
        fun socialLogin(map: Map<String, String>)
        fun emailVerificationApiCall(email: String)
    }
}