package com.capcorp.ui.signup.enter_password


import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.ui.signup.SignInActivity
import com.capcorp.ui.signup.resetpassword.ResetPasswordActivity
import com.capcorp.ui.user.homescreen.HomeActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.SignupModel
import kotlinx.android.synthetic.main.activity_contact_info.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.fragment_enter_password.*


class EnterPasswordFragment : Fragment(), EnterPasswordContract.View {

    private val presenter = EnterPasswordPresenter()

    private lateinit var dialogIndeterminate: DialogIndeterminate

    private var userDetail: SignupModel? = null

    private var UserType: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_enter_password, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(activity)
        tvName.text = arguments.getString(FIRST_NAME, "")
        tvPhoneNo.text =
            arguments.getString(COUNTRY_CODE, "") + " " + arguments.getString(PHONE_NUMBER, "")
        Glide.with(this).load(arguments.getString(PROFILE_PIC)).apply(RequestOptions().circleCrop())
            .apply(RequestOptions().placeholder(R.drawable.profile_pic_placeholder))
            .into(ivProfilePic)

        textView13.setOnClickListener {
            if (CheckNetworkConnection.isOnline(activity)) {
                presenter.sendOtpApiCall(
                    phoneNo = arguments.getString(COUNTRY_CODE, "") + "" + arguments.getString(
                        PHONE_NUMBER,
                        ""
                    ),
                    asSend = "resetPwd"
                )
            } else {
                CheckNetworkConnection.showNetworkError(contactRoot)
            }

        }
        UserType = arguments.getString("type").toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }

    override fun onResume() {
        super.onResume()
        (activity as SignInActivity).fab.setOnClickListener(fabClickListener)
    }

    private val fabClickListener = View.OnClickListener {
        val map = HashMap<String, String>()
        val bundle = arguments
        map[FULL_NUMBER] = bundle.getString(COUNTRY_CODE) + "" + bundle.getString(PHONE_NUMBER)
        map[LAT] = "0"
        map[LONG] = "0"
        map[PASSWORD] = etPassword.text.toString()
        map[DEVICE_TYPE] = ANDROID
        if (UserType.equals("USER")) {
            map["type"] = "USER"
        } else {
            map["type"] = "DRIVER"
        }

        val languageId: String = if (SharedPrefs.with(activity).getString(PREF_LANG, "en") == "es")
            "1"
        else
            "0"

        map[LANGUAGE_ID] = languageId
        map[DEVICE_TOKEN] = SharedPrefs.with(activity).getString(DEVICE_TOKEN, "")
        presenter.loginApiCall(map)
    }


    override fun onLoginSuccess(data: SignupModel) {
        SharedPrefs.with(activity).save(ACCESS_TOKEN, data.accessToken)
        SharedPrefs.with(activity).save(USER_ID, data._id)
        SharedPrefs.with(activity).save(USER_TYPE, data.type)
        SharedPrefs.with(activity).save(USER_DATA, data)
        // SharedPrefs.with(activity).save(IS_MUTE, data.isMute)
        if (data.type.equals("USER")) {
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
            activity.finishAffinity()
        } else if (data.type.equals("DRIVER")) {
            val intent = Intent(activity, com.capcorp.ui.driver.homescreen.HomeActivity::class.java)
            startActivity(intent)
            activity.finishAffinity()
        } else {
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
            activity.finishAffinity()
        }
    }

    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun apiFailure() {
        frameLayout.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        frameLayout.showSnack(errorBody!!)
    }

    override fun validationsFailure(type: String?) {
        when (type) {
            Validations.FIELD_EMPTY -> {
                frameLayout.showSnack(R.string.please_enter_password)
            }
            Validations.FIELD_INVALID -> {
                frameLayout.showSnack(R.string.password_length_validation_msg)
            }
        }
    }

    override fun sendOtpSuccess() {
        startActivity(
            Intent(
                activity,
                ResetPasswordActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        )
    }

}
