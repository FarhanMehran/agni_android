package com.capcorp.ui.user_signup.verification_code

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.user.homescreen.HomeActivity
import com.capcorp.ui.user_signup.createPassword.CreatePasswordActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.SignupModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_contact_info.*
import kotlinx.android.synthetic.main.activity_verification_code.*
import kotlinx.android.synthetic.main.fragment_phone_verification.etOtp
import kotlinx.android.synthetic.main.fragment_phone_verification.tvCodeSentTo
import kotlinx.android.synthetic.main.fragment_phone_verification.tvResendCode
import org.json.JSONException


class VerificationCodeActivity : BaseActivity(), PhoneVerificationContract.View {

    private val TAG = javaClass.simpleName
    private lateinit var dialogIndeterminate: DialogIndeterminate
    private var fcmVerificationId: String? = ""
    private val presenter = PhoneVerficationPresenter()
    private var firstName: String = ""
    private var lastName: String = ""
    private var mDateOfBorth: String = ""
    private var mCountry: String = ""
    private var countryIso: String = ""
    private var type: String = ""
    private var email: String = ""
    private var countryCode: String = ""
    private var phoneNumber: String = ""
    private var requestType: String = ""
    private var profilePic: String = ""
    private var key: String = ""
    private var socialType: String = ""
    private var pn: String = ""
    private var userData: SignupModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification_code)
        presenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(this)

        userData = SharedPrefs.with(this).getObject(USER_DATA, SignupModel::class.java)


        //    pn = userData?.phoneNo.toString()
        tvCodeSentTo?.text =
            getString(R.string.code_sent_to) + " " + intent.extras?.getString(COUNTRY_CODE) +
                    " " + intent.extras?.getString(PHONE_NUMBER)
        val string = getString(R.string.didnt_received_the_code)
        val resendCodeString = getString(R.string.resend_code)
        val spannable = SpannableStringBuilder(string)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.here2dare_green)),
            string.indexOf(resendCodeString),
            string.indexOf(resendCodeString) + resendCodeString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            string.indexOf(resendCodeString),
            string.indexOf(resendCodeString) + resendCodeString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            MyClickableSpan(),
            string.indexOf(resendCodeString),
            string.indexOf(resendCodeString) + resendCodeString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvResendCode.setText(spannable, TextView.BufferType.SPANNABLE)
        tvResendCode.movementMethod = LinkMovementMethod.getInstance()


        getData()

        ivBackVerify.setOnClickListener { finish() }

    }

    private fun getData() {

        requestType = intent.getStringExtra("requestType").toString()

        if (requestType != "VerificationNumberType") {

            pn = intent.getStringExtra(PHONE_NUMBER).toString()
            firstName = intent.getStringExtra(FIRST_NAME).toString()
            lastName = intent.getStringExtra(LAST_NAME).toString()
            mDateOfBorth = intent.getStringExtra(DATE_OF_BIRTH).toString()
            type = intent.getStringExtra(USERTYPE).toString()
            mCountry = intent.getStringExtra(COUNTRY).toString()
            countryIso = intent.getStringExtra(COUNTRY_ISO).toString()
            email = intent.getStringExtra(EMAIL).toString()
            countryCode = intent.extras!!.getString(COUNTRY_CODE)!!
            phoneNumber = intent.extras!!.getString(PHONE_NUMBER)!!
            profilePic = intent.getStringExtra(PROFILE_PIC).toString()
            key = intent.getStringExtra("key").toString()
            socialType = intent.getStringExtra(SOCIAL_TYPE).toString()

        } else {

            pn = intent.getStringExtra(PHONE_NUMBER).toString()


            if (CheckNetworkConnection.isOnline(this@VerificationCodeActivity)) {

                presenter.sendOtpApiCall(
                    phoneNo = intent.extras?.getString(COUNTRY_CODE) + "" + intent.extras?.getString(
                        PHONE_NUMBER
                    ),
                    oldNumber = userData?.countryCode + userData?.phoneNo,
                    asSend = "phoneUpdate"
                )


            } else {
                CheckNetworkConnection.showNetworkError(contactRoot)
            }
        }
    }

    private val phoneVerificationCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                fcmVerificationId = verificationId
                Log.e("AutoRetrievalTimeOut", verificationId)
            }

            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                super.onCodeAutoRetrievalTimeOut(p0)
                Log.e("AutoRetrievalTimeOut", "Called")

            }

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                Toast.makeText(
                    this@VerificationCodeActivity,
                    getString(R.string.phone_verified_successfully),
                    Toast.LENGTH_LONG
                ).show()
                if (requestType.equals("signup")) {
                    openCreatePassword()
                } else {

                }
                Log.e("onVerificationCompleted", "Called")
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Log.e("onVerificationFailed", "Called")
                verificationRoot.showSnack("Enter valid verification code")
            }
        }

    override fun onResume() {
        super.onResume()
        fab_next_click.setOnClickListener(fabClickListener)
    }

    private val fabClickListener = View.OnClickListener {
        if (!etOtp.text.toString().trim().isEmpty()) {


            if (CheckNetworkConnection.isOnline(this)) {

                if (requestType == "VerificationNumberType")
                    presenter.verifyOtpInUpdateCase(
                        phoneNo = intent.getStringExtra(COUNTRY_CODE) + "" + intent.getStringExtra(
                            PHONE_NUMBER
                        ),
                        otp = etOtp.text.toString().trim(),
                        oldNumber = intent.getStringExtra(OLD_PHONE_NUMBER).toString(),
                        countryISO = intent.getStringExtra(COUNTRY_ISO).toString()
                    )
                else
                    presenter.verifyApiCall(
                        intent.getStringExtra(COUNTRY_CODE) + "" + pn,
                        etOtp.text.toString().trim()
                    )

            } else {
                CheckNetworkConnection.showNetworkError(contactRoot)
            }

        } else {

            verificationRoot.showSnack(R.string.please_enter_otp)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential as AuthCredential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = task.result?.user
                    Log.e(TAG, "signInWithCredential:success" + user?.phoneNumber)
                    openCreatePassword()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.e(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        verificationRoot.showSnack(R.string.invalide_code)
                    } else {
                        verificationRoot.showSnack(R.string.error_in_verifying_phone)
                    }
                }
            }
    }

    private fun openCreatePassword() {
        if (type.equals("USER")) {
            val intent = Intent(applicationContext, CreatePasswordActivity::class.java)
            intent.putExtra(COUNTRY_CODE, countryCode)
            intent.putExtra(PHONE_NUMBER, phoneNumber)
            intent.putExtra(FIRST_NAME, firstName)
            intent.putExtra(LAST_NAME, lastName)
            intent.putExtra(DATE_OF_BIRTH, mDateOfBorth)
            intent.putExtra(USERTYPE, type)
            intent.putExtra(COUNTRY, mCountry)
            intent.putExtra(EMAIL, email)
            intent.putExtra(COUNTRY_ISO, countryIso)
            startActivity(intent)
        } else {
            val intent = Intent(applicationContext, CreatePasswordActivity::class.java)
            intent.putExtra(COUNTRY_CODE, countryCode)
            intent.putExtra(PHONE_NUMBER, phoneNumber)
            intent.putExtra(FIRST_NAME, firstName)
            intent.putExtra(LAST_NAME, lastName)
            intent.putExtra(DATE_OF_BIRTH, mDateOfBorth)
            intent.putExtra(USERTYPE, type)
            intent.putExtra(COUNTRY, mCountry)
            intent.putExtra(EMAIL, email)
            intent.putExtra(COUNTRY_ISO, countryIso)
            startActivity(intent)
        }

    }

    inner class MyClickableSpan : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }

        override fun onClick(widget: View) {

            if (CheckNetworkConnection.isOnline(this@VerificationCodeActivity)) {

                if (requestType == "VerificationNumberType")
                    presenter.sendOtpApiCall(
                        phoneNo = intent.extras?.getString(COUNTRY_CODE) + "" + intent.extras?.getString(
                            PHONE_NUMBER
                        ),
                        oldNumber = userData?.countryCode + userData?.phoneNo,
                        asSend = "phoneUpdate"
                    )
                else
                    presenter.sendOtpApiCall(
                        phoneNo = intent.extras?.getString(COUNTRY_CODE) + "" + intent.extras?.getString(
                            PHONE_NUMBER
                        ),
                        oldNumber = null,
                        asSend = "SignUp"
                    )

            } else {
                CheckNetworkConnection.showNetworkError(contactRoot)
            }

        }

    }

    override fun otpSuccess() {
        Toast.makeText(this, getString(R.string.code_been_sent_to_number), Toast.LENGTH_SHORT)
            .show()
    }

    override fun apiSuccess(isPhoneNoExists: Boolean, data: SignupModel) {
    }

    override fun verifyOtpSuccess() {
        when (requestType) {
            "signup" -> {
                openCreatePassword()
            }
            "VerificationNumberType" -> {
                val returnIntent = Intent()
                returnIntent.putExtra("result", true)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
            else -> {

                try {
                    val map = HashMap<String, String>()
                    map[SOCIAL_LOGIN_KEY] = key
                    map[FIRST_NAME] = firstName
                    map[LAST_NAME] = lastName
                    map[PROFILE_PIC_URL] = profilePic
                    map[SOCIAL_TYPE] = socialType
                    map[EMAIL] = email
                    map[DEVICE_TYPE] = ANDROID
                    map[COUNTRY_CODE] = countryCode
                    map[PHONE_NUMBER] = phoneNumber
                    map[COUNTRY_ISO] = countryIso
                    if (type.equals("USER")) {
                        map[SOCIAL_LOGIN_TYPE] = "USER"
                    } else {
                        map[SOCIAL_LOGIN_TYPE] = "DRIVER"
                    }
                    map[DEVICE_TOKEN] = SharedPrefs.with(this).getString(DEVICE_TOKEN, "")

                    val languageId: String =
                        if (SharedPrefs.with(this).getString(PREF_LANG, "en") == "es")
                            "1"
                        else
                            "0"

                    map[LANGUAGE_ID] = languageId
                    presenter.socialLogin(map)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }
    }

    override fun apiSuccess(data: SignupModel?) {
        if (data?.userExists == true) {
            SharedPrefs.with(this).save(ACCESS_TOKEN, data.accessToken)
            SharedPrefs.with(this).save(USER_ID, data._id)
            SharedPrefs.with(this).save(USER_TYPE, data.type)
            SharedPrefs.with(this).save(USER_DATA, data)

            if (data.type.equals(UserType.USER)) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finishAffinity()
            } else {
                val intent =
                    Intent(this, com.capcorp.ui.driver.homescreen.HomeActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }

            /*val intent = Intent(this, ThanksTravellerActivity::class.java)
            intent.putExtra("type", data.type)
            startActivity(intent)
            finishAffinity()*/


            /*if (data.type.equals("USER")) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finishAffinity()

            } else if (data.type.equals("DRIVER")) {
                val intent = Intent(this, com.capcorp.ui.driver.homescreen.HomeActivity::class.java)
                startActivity(intent)
                finishAffinity()
            } else {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }*/
        }
    }

    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun apiFailure() {
        verificationRoot.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        verificationRoot.showSnack(errorBody!!)

    }

    override fun validationsFailure(type: String?) {

    }

}
