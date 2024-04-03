package com.capcorp.ui.signup.create_password


import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capcorp.R
import com.capcorp.ui.signup.phone_verification.CreatePasswordContract
import com.capcorp.ui.signup.phone_verification.CreatePasswordPresenter
import com.capcorp.ui.user.homescreen.HomeActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.SignupModel
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.fragment_create_password.*


class CreatePasswordFragment : Fragment(), CreatePasswordContract.View {
    override fun onDocumentUploadedSuccess(data: SignupModel?) {
    }

    override fun onTravellerSignUpSuccess(data: SignupModel?) {

    }

    override fun onSignupSuccess(data: SignupModel?) {
        SharedPrefs.with(activity).save(ACCESS_TOKEN, data?.accessToken)
        data?.localType = "USER"
        SharedPrefs.with(activity).save(USER_DATA, data)
        val intent = Intent(activity, HomeActivity::class.java)
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        activity.finishAffinity()
    }

    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun showMessage(message: String): String {
        activity.signupRoot.showSWWerror(message)
        return super.showMessage(message)
    }

    override fun apiFailure() {

    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun validationsFailure(type: String?) {
        when (type) {
            Validations.FIELD_EMPTY -> {
                activity.signupRoot.showSnack(R.string.please_enter_password)
            }
            Validations.FIELD_INVALID -> {
                activity.signupRoot.showSnack(R.string.password_length_validation_msg)
            }
        }
    }

    private val presenter = CreatePasswordPresenter()

    private var showPassword = false

    private lateinit var dialogIndeterminate: DialogIndeterminate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_password, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(activity)
        setListeners()
    }

    private fun setListeners() {
        tvShow.setOnClickListener { showPassword() }
    }

    private fun showPassword() {
        if (etPassword.transformationMethod is PasswordTransformationMethod) {
            etPassword.transformationMethod = null
            tvShow.setText(R.string.hide)
            showPassword = false
            etPassword.setSelection(etPassword.text.toString().length)
        } else {
            tvShow.setText(R.string.show)
            etPassword.transformationMethod = PasswordTransformationMethod()
            showPassword = true
            etPassword.setSelection(etPassword.text.toString().length)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        showLoader(false)
        presenter.detachView()
    }

    private val fabClickListener = View.OnClickListener {
        val map = HashMap<String, String>()
        val bundle = arguments
        map[COUNTRY_CODE] = bundle.getString(COUNTRY_CODE).toString()
        map[FULL_NUMBER] = bundle.getString(PHONE_NUMBER).toString()
        map[FIRST_NAME] = bundle.getString(FIRST_NAME).toString()
        map[LAST_NAME] = bundle.getString(LAST_NAME).toString()
        map[EMAIL] = bundle.getString(EMAIL).toString()
        map[LAT] = "0"
        map[LONG] = "0"
        map[DEFAULT_LAT] = "0"
        map[DEFAULT_LONG] = "0"
        map[PASSWORD] = etPassword.text.toString()
        map[DEVICE_TYPE] = ANDROID

        val languageId: String = if (SharedPrefs.with(activity).getString(PREF_LANG, "en") == "es")
            "1"
        else
            "0"


        map[LANGUAGE_ID] = languageId
        map[DEVICE_TOKEN] = SharedPrefs.with(activity).getString(DEVICE_TOKEN, "")
        presenter.signupApiCall(map)


    }

    override fun onResume() {
        super.onResume()
        activity.fab.setOnClickListener(fabClickListener)
    }

    override fun onPause() {
        super.onPause()
        activity.fab.setOnClickListener(null)
    }


}
