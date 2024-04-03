package com.capcorp.ui.signup.phone_verification


import android.app.Fragment
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.capcorp.R
import com.capcorp.ui.user.homescreen.HomeActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.SignupModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.fragment_phone_verification.*
import java.util.concurrent.TimeUnit


class PhoneVerificationFragment : Fragment(), PhoneVerificationContract.View {
    override fun apiSuccess(data: SignupModel) {
        SharedPrefs.with(activity).save(ACCESS_TOKEN, data.accessToken)
        SharedPrefs.with(activity).save(USER_ID, data._id)
        SharedPrefs.with(activity).save(USER_TYPE, "USER")

        data.localType = "USER"
        SharedPrefs.with(activity).save(USER_DATA, data)
        val intent = Intent(activity, HomeActivity::class.java)
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        activity.finishAffinity()

    }

    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun apiFailure() {
        activity.signupRoot.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {

    }

    override fun validationsFailure(type: String?) {

    }

    private val TAG = javaClass.simpleName

    private lateinit var dialogIndeterminate: DialogIndeterminate

    private var phoneNo: String = ""

    private var fcmVerificationId: String? = ""

    private var handler = Handler()

    private val presenter = PhoneVerficationPresenter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_verification, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(activity)
        handler = Handler()
        handler.postDelayed(Runnable {
            if (phoneNo.isEmpty()) {
                phoneNo = arguments.getString(COUNTRY_CODE) + arguments.getString(PHONE_NUMBER)
                /* PhoneAuthProvider.getInstance().verifyPhoneNumber("+918984729443", 120, TimeUnit.SECONDS, activity,
                         phoneVerificationCallback)*/
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNo, 120, TimeUnit.SECONDS, activity,
                    phoneVerificationCallback
                )

            }
        }, 1200)

        tvCodeSentTo?.text =
            getString(R.string.code_sent_to) + " " + arguments.getString(COUNTRY_CODE) +
                    " " + arguments.getString(PHONE_NUMBER)
        val string = getString(R.string.didnt_received_the_code)
        val resendCodeString = getString(R.string.resend_code)
        val spannable = SpannableStringBuilder(string)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(activity, R.color.app_green)),
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
                    activity,
                    getString(R.string.phone_verified_successfully),
                    Toast.LENGTH_LONG
                ).show()
                var bundle: Bundle? = null
                if (arguments != null) {
                    bundle = arguments
                } else {
                    bundle = Bundle()
                }
                var data = bundle!!.getSerializable("mapData")
                if (data != null) {
                    data = bundle.getSerializable("mapData") as HashMap<String, String>
                    data[FULL_NUMBER] = bundle.getString(COUNTRY_CODE) +
                            " " + bundle.getString(PHONE_NUMBER)

                    val languageId: String =
                        if (SharedPrefs.with(activity).getString(PREF_LANG, "en") == "es")
                            "1"
                        else
                            "0"
                    data[LANGUAGE_ID] = languageId
                    presenter.socialLogin(data)
                }


                Log.e("onVerificationCompleted", "Called")
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Log.e("onVerificationFailed", "Called")
                activity.signupRoot.showSnack("Enter valid verification code")
            }
        }


    override fun onResume() {
        super.onResume()
        activity.fab.setOnClickListener(fabClickListener)
    }


    override fun onDestroy() {
        super.onDestroy()
        FirebaseAuth.getInstance().removeAuthStateListener { phoneVerificationCallback }
    }

    private val fabClickListener = View.OnClickListener {
        if (!etOtp.text.toString().trim().isEmpty()) {
            val authProvider =
                PhoneAuthProvider.getCredential(fcmVerificationId!!, etOtp.text.toString())
            signInWithPhoneAuthCredential(authProvider)
        } else {
            activity.signupRoot.showSnack(R.string.please_enter_otp)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = task.result?.user
                    Log.e(TAG, "signInWithCredential:success" + user?.phoneNumber)

                    var bundle: Bundle? = null
                    if (arguments != null) {
                        bundle = arguments
                    } else {
                        bundle = Bundle()
                    }
                    var data = bundle!!.getSerializable("mapData")
                    if (data != null) {
                        data = bundle.getSerializable("mapData") as HashMap<String, String>
                        data[FULL_NUMBER] = bundle.getString(COUNTRY_CODE) +
                                " " + bundle.getString(PHONE_NUMBER)
                        presenter.socialLogin(data)
                    }

                    // ...
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.e(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        activity.signupRoot.showSnack(R.string.invalide_code)
                    } else {
                        activity.signupRoot.showSnack(R.string.error_in_verifying_phone)
                    }
                }
            }
    }



    inner class MyClickableSpan : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }

        override fun onClick(widget: View) {
            FirebaseAuth.getInstance().signOut()
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo, 120, TimeUnit.SECONDS, activity,
                phoneVerificationCallback
            )
            activity.signupRoot.showSnack(R.string.code_sent_successfyully)
        }

    }
}
