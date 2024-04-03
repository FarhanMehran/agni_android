package com.capcorp.ui.signup.login


import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capcorp.R
import com.capcorp.ui.signup.enter_password.EnterPasswordFragment
import com.capcorp.ui.user.homescreen.HomeActivity
import com.capcorp.ui.user_signup.signup_users.SignUpActivity
import com.capcorp.utils.*
import com.capcorp.utils.facebook.FacebookLogin
import com.capcorp.utils.facebook.FacebookLoginListener
import com.capcorp.webservice.models.SignupModel
import com.capcropdriver.ui.loginsignup.contactinfo.ContactInfoActivity
import com.facebook.FacebookException
import com.facebook.GraphResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.hbb20.CountryCodePicker
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_signup.fab
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class LoginFragment : Fragment(), LoginContract.View, View.OnClickListener {
    override fun apiEmailSuccess(isPhoneNoExists: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private val presenter = LoginPresenter()

    private lateinit var dialogIndeterminate: DialogIndeterminate

    private val TAG = javaClass.simpleName

    private var isPhoneNoValid: Boolean = false

    private lateinit var facebookLogin: FacebookLogin

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private val RC_SIGN_IN = 100

    private var Usertype: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)
        facebookLogin = FacebookLogin(activity, this)
        dialogIndeterminate = DialogIndeterminate(activity)
        //countryPicker.registerCarrierNumberEditText(etPhone)
        setListeners()
        Usertype = UserType.USER
        countryPicker.setCountryForNameCode("MX")
        if (SharedPrefs.with(activity).getString(PREF_LANG, "en") == "en")
            countryPicker?.changeDefaultLanguage(CountryCodePicker.Language.ENGLISH)
        else
            countryPicker?.changeDefaultLanguage(CountryCodePicker.Language.SPANISH)
    }

    private fun setListeners() {
        //countryPicker.setPhoneNumberValidityChangeListener { isPhoneNoValid = it }
        llFacebook.setOnClickListener(this)
        llGoogle.setOnClickListener(this)
        tv_signup.setOnClickListener(this)
        tv_didnt_account.setOnClickListener(this)
        facebookLogin.setFacebookLoginListener(facebookLoginListener)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.llFacebook -> {
                if (CheckNetworkConnection.isOnline(activity)) {
                    facebookLogin.performLogin()
                } else {
                    CheckNetworkConnection.showNetworkError(activity.signinRoot)
                }

            }
            R.id.llGoogle -> {
                if (CheckNetworkConnection.isOnline(activity)) {
                    signInGoogle()
                } else {
                    CheckNetworkConnection.showNetworkError(activity.signinRoot)
                }
            }
            R.id.tv_signup -> {
                startActivity(
                    Intent(activity, SignUpActivity::class.java).putExtra(
                        "fromType",
                        "true"
                    )
                )
            }
            R.id.tv_didnt_account -> {
                startActivity(
                    Intent(activity, SignUpActivity::class.java).putExtra(
                        "fromType",
                        "true"
                    )
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }

    override fun onDestroy() {
        super.onDestroy()
        SharedPrefs.with(activity).remove("ccp")
    }

    override fun onResume() {
        super.onResume()
        activity.fab.setOnClickListener(fabClickListener)
        if (SharedPrefs.with(activity).getString("ccp", "") != null) {
            countryPicker.setCountryForNameCode(SharedPrefs.with(activity).getString("ccp", ""))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
        } else {
            facebookLogin.onActivityResult(requestCode, resultCode, data)
        }
    }

    private val fabClickListener = View.OnClickListener {
        val phonelength: String = etPhone.text.toString().trim()
        if (phonelength.isEmpty()) {
            activity.signinRoot.showSnack(getString(R.string.please_enter_number))
        } else if (phonelength.length < 10) {
            activity.signinRoot.showSnack(R.string.enter_a_valid_phone)
        } else {
            presenter.phoneVerificationApiCall(
                countryPicker.selectedCountryCodeWithPlus + "" + etPhone.text.toString().trim(),
                Usertype
                    ?: "",
                "login"
            )
        }
    }

    private val facebookLoginListener = object : FacebookLoginListener {
        override fun onFbLoginSuccess() {
            facebookLogin.getUserProfile()
        }

        override fun onFbLoginCancel() {
            Log.e("fb login", "canceled")
        }

        override fun onFbLoginError(exception: FacebookException?) {
            activity.signinRoot.showSnack(exception!!.localizedMessage)
        }

        override fun onGetprofileSuccess(json: JSONObject?, response: GraphResponse?) {
            try {
                val id = json?.getString("id")
//                val name = json?.getString("name")
                val firstName = json?.getString("first_name")
                val lastName = json?.getString("last_name")
                var email: String = ""
                if (json?.has("email") == true) {
                    email = json.getString("email")
                }
                val picUrl = json?.getJSONObject("picture")?.getJSONObject("data")?.getString("url")
                /*val country = json?.getJSONObject("location")?.getString("country");
                val date_of_birth = json?.getString("user_birthday")*/
                val map = HashMap<String, String>()
                map[SOCIAL_LOGIN_KEY] = id ?: ""
                map[FIRST_NAME] = firstName ?: ""
                map[LAST_NAME] = lastName ?: ""
                map[PROFILE_PIC_URL] = picUrl ?: ""
                socialType = "Facebook"
                map[SOCIAL_TYPE] = socialType
                if (!email.isNullOrEmpty()) {
                    map[EMAIL] = email
                }
                map[DEVICE_TYPE] = ANDROID

                if (Usertype.equals("USER")) {
                    map[SOCIAL_LOGIN_TYPE] = "USER"
                } else {
                    map[SOCIAL_LOGIN_TYPE] = "DRIVER"
                }


                val languageId: String =
                    if (SharedPrefs.with(activity).getString(PREF_LANG, "en") == "es")
                        "1"
                    else
                        "0"



                map[LANGUAGE_ID] = languageId

                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            map[DEVICE_TOKEN] = ""
                            presenter.socialLogin(map)
                            return@OnCompleteListener
                        }

                        // Get new Instance ID token
                        map[DEVICE_TOKEN] = task.result?.token ?: ""
                        presenter.socialLogin(map)
                    })

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    override fun apiSuccess(userInfo: SignupModel?) {
        if (userInfo?.socialLogin == true) {
            if (userInfo.googleId?.isNotEmpty() == true) {
                activity.signinRoot.showSnack(R.string.already_registered_with_google)
            } else {
                activity.signinRoot.showSnack(R.string.already_registered_with_facebook)
            }
        } else if (userInfo?.userExists == true) {
            SharedPrefs.with(activity).save(PHONE_NUMBER, userInfo.phoneNo)
            SharedPrefs.with(activity).save(COUNTRY_CODE, userInfo.countryCode)
            SharedPrefs.with(activity).save("ccp", countryPicker.selectedCountryNameCode + "")

            val bundle = Bundle()
            bundle.putString("type", Usertype)
            bundle.putString(COUNTRY_CODE, userInfo.countryCode)
            bundle.putString(PHONE_NUMBER, userInfo.phoneNo)
            bundle.putString(FIRST_NAME, userInfo.fullName)
            bundle.putString(PROFILE_PIC, userInfo.profilePicURL?.original)
            val fragment = EnterPasswordFragment()
            fragment.arguments = bundle
            replaceFragment(fragmentManager, fragment, TAG)
        } else {
            activity.signinRoot.showSnack(R.string.user_not_registered)
        }
    }

    override fun socialLoginApiSuccess(userInfo: SignupModel?) {
        dialogIndeterminate.dismiss()
        if (userInfo?.userExists == true) {
            SharedPrefs.with(activity).save(ACCESS_TOKEN, userInfo.accessToken)
            SharedPrefs.with(activity).save(USER_ID, userInfo._id)
            SharedPrefs.with(activity).save(USER_TYPE, userInfo.type)
            SharedPrefs.with(activity).save(USER_DATA, userInfo)
            if (!userInfo.googleId.equals("")) {
                callLogout()
            }
            if (userInfo.type.equals("USER")) {
                val intent = Intent(activity, HomeActivity::class.java)
                startActivity(intent)
                activity.finishAffinity()

            } else if (userInfo.type.equals("DRIVER")) {
                val intent =
                    Intent(activity, com.capcorp.ui.driver.homescreen.HomeActivity::class.java)
                startActivity(intent)
                activity.finishAffinity()
            } else {
                val intent = Intent(activity, HomeActivity::class.java)
                startActivity(intent)
                activity.finishAffinity()
            }
        } else {
            val intent = Intent(activity, ContactInfoActivity::class.java)
            intent.putExtra(FIRST_NAME, userInfo?.firstName)
            intent.putExtra(LAST_NAME, userInfo?.lastName)
            intent.putExtra(USERTYPE, userInfo?.type)
            if (userInfo?.emailId != null)
                intent.putExtra(EMAIL, userInfo.emailId)
            else
                intent.putExtra(EMAIL, "")

            if (userInfo?.profilePicURL?.original != null)
                intent.putExtra(PROFILE_PIC, userInfo.profilePicURL.original)
            else
                intent.putExtra(PROFILE_PIC, "")
            intent.putExtra("key", userInfo?.key)
            intent.putExtra(SOCIAL_TYPE, userInfo?.socialType)
            intent.putExtra(DATE_OF_BIRTH, "")
            intent.putExtra(COUNTRY, "")
            intent.putExtra("requestType", "social_login")
            startActivity(intent)

        }

    }

    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun apiFailure() {
        activity.signinRoot.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        activity.signinRoot.showSnack(errorBody!!)
    }

    override fun validationsFailure(type: String?) {
        when (type) {
            Validations.FIELD_EMPTY -> {
                activity.signinRoot?.showSnack(R.string.please_enter_phonenumber)
            }
            Validations.FIELD_INVALID -> {
                activity.signinRoot?.showSnack(R.string.enter_a_valid_phone)
            }
        }
    }

    private fun signInGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private var socialType: String = ""

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val map = HashMap<String, String>()
            map[SOCIAL_LOGIN_KEY] = account?.id!!
            val tokenizer = StringTokenizer(account.displayName)
            map[FIRST_NAME] = tokenizer.nextToken()
            var lastName = tokenizer.nextToken()
            while (tokenizer.hasMoreTokens()) {
                lastName += tokenizer.nextToken()
            }
            map[LAST_NAME] = lastName

            //for testing
            if (account.photoUrl != null) {
                map[PROFILE_PIC_URL] = account.photoUrl.toString()
            }
            socialType = "Google"
            map[SOCIAL_TYPE] = socialType
            map[EMAIL] = account.email!!
            map[DEVICE_TYPE] = ANDROID
            if (Usertype.equals("USER")) {
                map[SOCIAL_LOGIN_TYPE] = "USER"
            } else {
                map[SOCIAL_LOGIN_TYPE] = "DRIVER"
            }
            map[DEVICE_TOKEN] = SharedPrefs.with(activity).getString(DEVICE_TOKEN, "")
            //((activity as SignInActivity)).showPhoneNumberFragment(map)
            presenter.socialLogin(map)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            activity.signinRoot.showSnack(R.string.unbale_to_connect_with_google)
        }
    }

    fun callLogout() {
        mGoogleSignInClient.signOut()
    }

}
