package com.capcorp.ui.signup.phone_verification

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.SignupModel

class EnterPhoneNoContract {
    interface View : BaseView {
        fun apiSuccess(isPhoneNoExists: Boolean, data: SignupModel)
        fun apiEmailSuccess(isPhoneNoExists: Boolean, data: SignupModel)
        fun sendOtpSuccess()
    }

    interface Presenter : BasePresenter<View> {
        fun phoneVerificationApiCall(phoneNo: String, type: String, asCheck: String)
        fun sendOtpApiCall(phoneNo: String, oldNumber: String?, asSend: String)
        fun socialLogin(map: Map<String, String>)
        fun emailVerificationApiCall(email: String)
    }
}