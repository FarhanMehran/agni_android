package com.capcorp.ui.user_signup.verification_code

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.SignupModel

class PhoneVerificationContract {


    interface View : BaseView {
        fun apiSuccess(data: SignupModel?)
        fun verifyOtpSuccess()
        fun apiSuccess(isPhoneNoExists: Boolean, data: SignupModel)
        fun otpSuccess()
    }

    interface Presenter : BasePresenter<View> {
        fun socialLogin(map: Map<String, String>)
        fun verifyApiCall(phoneNo: String, otp: String)
        fun verifyOtpInUpdateCase(
            phoneNo: String,
            otp: String,
            oldNumber: String,
            countryISO: String
        )

        fun sendOtpApiCall(phoneNo: String, oldNumber: String?, asSend: String)
    }
}