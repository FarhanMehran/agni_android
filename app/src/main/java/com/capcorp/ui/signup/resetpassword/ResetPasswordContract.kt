package com.capcorp.ui.signup.resetpassword

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.UserDataDto

class ResetPasswordContract {
    interface View : BaseView {
        fun apiSuccess(data: UserDataDto?)
    }

    interface Presenter : BasePresenter<View> {
        fun hitApi(otp: String, phoneNo: String, newPassword: String)

    }

}
