package com.capcropdriver.ui.loginsignup.contactinfo

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.signup.phone_verification.EnterPhoneNoContract
import com.capcorp.ui.signup.phone_verification.EnterPhoneNoPresenter
import com.capcorp.ui.user_signup.verification_code.VerificationCodeActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.SignupModel
import com.hbb20.CountryCodePicker
import kotlinx.android.synthetic.main.activity_contact_info.*
import java.util.regex.Pattern

class ContactInfoActivity : BaseActivity(), View.OnClickListener, EnterPhoneNoContract.View {

    private val TAG = javaClass.simpleName

    private val presenter = EnterPhoneNoPresenter()

    private lateinit var dialogIndeterminate: DialogIndeterminate

    private var isPhoneNoValid: Boolean = false

    private var firstName: String = ""
    private var lastName: String = ""
    private var mDateOfBirth: String = ""
    private var mCountry: String = ""
    private var type: String = ""
    private var email: String = ""
    private var requestType: String = ""
    private var profilePic: String = ""
    private var key: String = ""
    private var socialType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_info)
        presenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(this)
        countryPicker.registerCarrierNumberEditText(etPhone)
        countryPicker.setCountryForNameCode("MX")
        if (SharedPrefs.with(this).getString(PREF_LANG, "en") == "en")
            countryPicker?.changeDefaultLanguage(CountryCodePicker.Language.ENGLISH)
        else
            countryPicker?.changeDefaultLanguage(CountryCodePicker.Language.SPANISH)
        getData()
        setData()
        clickListeners()
    }

    private fun setData() {

        if (requestType.equals("social_login") && email.isNotEmpty()) {
            etEmail.setText(email)
        } else {
            etEmail.isFocusable = true
        }
    }

    private fun getData() {
        requestType = intent.getStringExtra("requestType").toString()
        firstName = intent.getStringExtra(FIRST_NAME).toString()
        lastName = intent.getStringExtra(LAST_NAME).toString()
        mDateOfBirth = intent.getStringExtra(DATE_OF_BIRTH).toString()
        mCountry = intent.getStringExtra(COUNTRY).toString()
        type = intent.getStringExtra(USERTYPE).toString()
        email = intent.getStringExtra(EMAIL).toString()
        profilePic = intent.getStringExtra(PROFILE_PIC).toString()
        key = intent.getStringExtra("key").toString()
        socialType = intent.getStringExtra(SOCIAL_TYPE).toString()
    }

    private fun clickListeners() {
        ivBack.setOnClickListener(this)
        next.setOnClickListener(this)
        //countryPicker.setPhoneNumberValidityChangeListener { isPhoneNoValid = it }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }

            R.id.next -> {
                if (isValid()) {
                    if (CheckNetworkConnection.isOnline(this)) {
                        if (type.equals("USER")) {
                            presenter.phoneVerificationApiCall(
                                countryPicker.selectedCountryCodeWithPlus + "" + phone, "USER", "signUp"
                            )
                        } else {
                            presenter.phoneVerificationApiCall(
                                countryPicker.selectedCountryCodeWithPlus + "" +phone, "DRIVER", "signUp"
                            )
                        }


                    } else {
                        CheckNetworkConnection.showNetworkError(contactRoot)
                    }

                }
            }
        }
    }

    private var phone: String = ""

    private fun isValid(): Boolean {
        phone = etPhone.text.toString().trim().replace(" ","")

        val temp = phone.replace("0", "")
        if (phone.isEmpty()) {
            etPhone.showSnack(getString(R.string.please_enter_phonenumber))
            etPhone.requestFocus()
            return false
        } else if ((phone.length < 5) || (phone.length > 16)) {
            etPhone.showSnack(R.string.enter_a_valid_phone)
            etPhone.requestFocus()
            return false
        } else if (temp.isEmpty()) {
            etPhone.showSnack(R.string.enter_a_valid_phone)
            etPhone.requestFocus()
            etPhone.setText("")
            return false
        }
        return true
    }

    private fun checkEmailFormat(): Boolean {
        val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    override fun apiSuccess(isPhoneNoExists: Boolean, data: SignupModel) {
        if (!isPhoneNoExists) {

            if (CheckNetworkConnection.isOnline(this)) {
                presenter.sendOtpApiCall(
                    phoneNo = countryPicker.selectedCountryCodeWithPlus + "" + phone,
                    oldNumber = null, asSend = "signUp"
                )
            } else {
                CheckNetworkConnection.showNetworkError(contactRoot)
            }


        } else {
            contactRoot.showSnack(R.string.phone_number_already_exists)
        }
    }

    override fun apiEmailSuccess(isPhoneNoExists: Boolean, data: SignupModel) {
        if (!isPhoneNoExists) {
            presenter.sendOtpApiCall(
                phoneNo = countryPicker.selectedCountryCodeWithPlus + "" + phone,
                oldNumber = null, asSend = "signUp"
            )
        } else {
            contactRoot.showSnack(R.string.email_already_exists)
        }
    }

    override fun sendOtpSuccess() {

        val intent = Intent(applicationContext, VerificationCodeActivity::class.java)
        intent.putExtra(COUNTRY_CODE, countryPicker.selectedCountryCodeWithPlus)
        intent.putExtra(COUNTRY_ISO, countryPicker?.selectedCountryNameCode)
        intent.putExtra(PHONE_NUMBER, phone)
        intent.putExtra(FIRST_NAME, firstName)
        intent.putExtra(LAST_NAME, lastName)
        intent.putExtra(DATE_OF_BIRTH, mDateOfBirth)
        intent.putExtra(COUNTRY, mCountry)
        intent.putExtra(USERTYPE, type)
        intent.putExtra(EMAIL, email)
        intent.putExtra("requestType", requestType)
        intent.putExtra(PROFILE_PIC, profilePic)
        intent.putExtra("key", key)
        intent.putExtra(SOCIAL_TYPE, socialType)
        startActivity(intent)
    }

    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun apiFailure() {
        contactRoot.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        contactRoot.showSnack(errorBody!!)
    }

    override fun validationsFailure(type: String?) {
        when (type) {
            Validations.FIELD_EMPTY -> {
                contactRoot?.showSnack(R.string.please_enter_phonenumber)
            }
            Validations.FIELD_INVALID -> {
                contactRoot?.showSnack(R.string.enter_a_valid_phone)
            }
        }
    }
}
