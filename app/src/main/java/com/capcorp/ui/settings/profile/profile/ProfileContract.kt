package com.capcorp.ui.settings.profile.profile

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.SignupModel
import com.capcorp.webservice.models.image_upload.ImageUploadModel
import java.io.File

class ProfileContract {

    interface View : BaseView {
        fun onEditProfileApiSuccess(data: SignupModel?)
        fun onUploadImageSuccess(uploadModel: ImageUploadModel)
        fun onValidationsSuccess(map: Map<String, String>)
        fun apiEmailSuccess(_isEmailExists: Boolean)
        fun apiNumberSuccess(isPhoneNoExists: Boolean)

    }

    interface Presenter : BasePresenter<View> {
        fun editProfileApiCall(authToken: String, map: Map<String, String>)
        fun imageUploadCall(file: File)
        fun emailVerificationApiCall(email: String)
        fun phoneVerificationApiCall(phoneNo: String, type: String, asCheck: String)


    }
}