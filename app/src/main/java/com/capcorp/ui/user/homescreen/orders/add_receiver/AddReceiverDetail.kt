package com.capcorp.ui.user.homescreen.orders.add_receiver

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.user.homescreen.Anys.add_receiver.ReceiverPresenter
import com.capcorp.utils.*
import com.capcorp.webservice.models.orders.ReceiverInfo
import com.capcorp.webservice.models.request_model.AddReciverRequest
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_reciver_detail.*

class AddReceiverDetail : BaseActivity(), View.OnClickListener, ReceiverContract.View {


    private var flagChange: Boolean = false
    private var presenter = ReceiverPresenter()
    private var phoneNumber: String = ""
    private var email: String = ""
    private var name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reciver_detail)
        presenter.attachView(this)
        clickListener()
        setView()
    }

    private fun setView() {
        if (intent.hasExtra(RECEIVER_INFO)) {
            val reciverInfo =
                Gson().fromJson(intent.getStringExtra(RECEIVER_INFO), ReceiverInfo::class.java)
            etEmail.setText(reciverInfo.emailId)
            etPhoneNumber.setText(reciverInfo.phoneNumber)
            etName.setText(reciverInfo.employeeName)
        }

    }

    private fun clickListener() {
        tvBack.setOnClickListener(this)
        tvSubmit.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvBack -> {
                onBackPressed()
            }
            R.id.tvSubmit -> {
                if (CheckNetworkConnection.isOnline(this)) {
                    checkValidation()
                }
            }
        }
    }

    private fun checkValidation() {
        email = etEmail.text.toString().trim()
        phoneNumber = etPhoneNumber.text.toString().trim()
        name = etName.text.toString().trim()

        val temp = phoneNumber.replace("0", "")

        if (phoneNumber.isEmpty()) {
            frameLayout_receiver_info.showSnack(getString(R.string.please_enter_phonenumber))
        } else if (phoneNumber.length < 6 && phoneNumber.length > 16) {
            frameLayout_receiver_info.showSnack(getString(R.string.enter_a_valid_phone))
        } else if (!isValidMobile(phoneNumber)) {
            frameLayout_receiver_info.showSnack(getString(R.string.enter_a_valid_phone))
        } else if (temp.isEmpty()) {
            frameLayout_receiver_info.showSnack(getString(R.string.enter_a_valid_phone))
        } else if (email.isEmpty()) {
            frameLayout_receiver_info.showSnack(getString(R.string.please_enter_email))
        } else if ((!Patterns.EMAIL_ADDRESS.matcher(email.trim { it <= ' ' }).matches())) {
            frameLayout_receiver_info.showSnack(getString(R.string.email_validation_msg))
        } else if (name.isEmpty()) {
            frameLayout_receiver_info.showSnack(getString(R.string.please_enter_firstname))
        } else {
            val AddReciverRequest = AddReciverRequest(
                orderId = intent.getStringExtra(ORDER_ID),
                emailId = email, phoneNo = phoneNumber, employeeName = name
            )
            presenter.apiAddReciver(getAuthAccessToken(this), AddReciverRequest)
        }

    }

    private fun isValidMobile(phone: String): Boolean {
        return android.util.Patterns.PHONE.matcher(phone).matches()
    }

    private fun dataVaild(): Boolean {
        email = etEmail.text.toString().trim()
        phoneNumber = etPhoneNumber.text.toString().trim()
        name = etName.text.toString().trim()
        return if (phoneNumber.isEmpty()) {
            frameLayout_receiver_info.showSnack(getString(R.string.please_phone_phone_number))
            return false
        } else if (phoneNumber.length < 8) {
            frameLayout_receiver_info.showSnack(getString(R.string.error_in_verifying_phone))
            return false
        } else if (email.isEmpty()) {
            frameLayout_receiver_info.showSnack(getString(R.string.please_enter_email))
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim { it <= ' ' }).matches()) {
            frameLayout_receiver_info.showSnack(getString(R.string.email_validation_msg))
            return false
        } else if (name.isEmpty()) {
            frameLayout_receiver_info.showSnack(getString(R.string.please_enter_receiver_name))
            return false
        } else true

    }


    override fun onBackPressed() {
        val resultIntent = Intent()
        if (flagChange) {
            resultIntent.putExtra("EmployeePhone", phoneNumber)
            resultIntent.putExtra("EmployeeEmail", email)
            resultIntent.putExtra("EmployeeName", name)
            setResult(Activity.RESULT_OK, resultIntent)
        } else {
            setResult(Activity.RESULT_CANCELED, resultIntent)
        }
        finish()
    }

    override fun showLoader(isLoading: Boolean) {

    }

    override fun apiFailure() {
        frameLayout_receiver_info.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        frameLayout_receiver_info?.showSnack(errorBody!!)
    }

    override fun validationsFailure(type: String?) {
    }

    override fun apiSuccess() {
        flagChange = true
        onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }


}
