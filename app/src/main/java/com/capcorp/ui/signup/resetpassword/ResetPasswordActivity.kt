package com.capcorp.ui.signup.resetpassword

import android.os.Bundle
import android.view.View
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.UserDataDto
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : BaseActivity(), View.OnClickListener, ResetPasswordContract.View {

    private val presenter = ResetPasswordPresenter()
    private lateinit var dialogIndeterminate: DialogIndeterminate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        dialogIndeterminate = DialogIndeterminate(this)
        presenter.attachView(this)
        clickListeners()
    }

    private fun clickListeners() {
        ivBack.setOnClickListener(this)
        next.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.next -> {
                if (isValidData()) {
                    if (CheckNetworkConnection.isOnline(this)) {
                        val phoneNo = SharedPrefs.with(this).getString(PHONE_NUMBER, "")
                        val countryCode = SharedPrefs.with(this).getString(COUNTRY_CODE, "")
                        presenter.hitApi(
                            etOtp.text?.trim().toString(),
                            countryCode + "" + phoneNo,
                            newPassword
                        )
                    } else {
                        CheckNetworkConnection.showNetworkError(enterPasswordRoot)
                    }
                }
            }

            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    private var newPassword = ""
    private var confirmPassword = ""
    private var otp = ""

    private fun isValidData(): Boolean {
        newPassword = etNew.text?.trim().toString()
        confirmPassword = etConfirm.text?.trim().toString()
        otp = etOtp.text?.trim().toString()

        if (otp.isEmpty()) {
            etOtp.showSnack(getString(R.string.please_enter_otp))
            etOtp.requestFocus()
            return false
        }

        if (otp.length < 4) {
            etOtp.showSnack(getString(R.string.please_enter_valid_otp))
            etOtp.requestFocus()
            return false
        }

        if (newPassword.isEmpty()) {
            etNew.showSnack(getString(R.string.please_enter_password))
            etNew.requestFocus()
            return false
        }
        if (confirmPassword.isEmpty()) {
            etConfirm.showSnack(getString(R.string.enter_confirm_pass))
            etConfirm.requestFocus()
            return false
        }

        if (confirmPassword.length < 6) {
            showToast(applicationContext, getString(R.string.pass_length_msg))
            return false

        }

        if (newPassword != confirmPassword) {
            etConfirm.showSnack(getString(R.string.pass_missmatch))
            etConfirm.requestFocus()
            return false
        }


        return true
    }

    override fun apiSuccess(data: UserDataDto?) {
        onBackPressed()
    }

    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        enterPasswordRoot.showSnack(errorBody!!)
    }

    override fun validationsFailure(type: String?) {

    }

    override fun apiFailure() {
        enterPasswordRoot.showSWWerror()
    }
}
