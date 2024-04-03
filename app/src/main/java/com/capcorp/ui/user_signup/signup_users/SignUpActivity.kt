package com.capcorp.ui.user_signup.signup_users

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.signup.SignInActivity
import com.capcorp.ui.signup.login.LoginContract
import com.capcorp.ui.signup.login.LoginPresenter
import com.capcorp.ui.user.homescreen.HomeActivity
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
import com.google.android.gms.tasks.Task
import com.hbb20.CountryCodePicker
import kotlinx.android.synthetic.main.activity_contact_info.*
import kotlinx.android.synthetic.main.layout_sign_up.*
import kotlinx.android.synthetic.main.layout_sign_up.ivBack
import kotlinx.android.synthetic.main.layout_signup.*
import kotlinx.android.synthetic.main.layout_signup.next
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class SignUpActivity : BaseActivity(), View.OnClickListener, OnDateSelectedListener,
    LoginContract.View {

    private var dialogAlert: AlertDialog? = null
    private var countryName: String = "Mexico"

    private val presenter = LoginPresenter()

    private lateinit var dialogIndeterminate: DialogIndeterminate

    private val TAG = javaClass.simpleName

    private var isPhoneNoValid: Boolean = false

    private lateinit var facebookLogin: FacebookLogin

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private val RC_SIGN_IN = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_signup)
        presenter.attachView(this)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        facebookLogin = FacebookLogin(this, this)
        dialogIndeterminate = DialogIndeterminate(this)
        countryName = edlCountry.defaultCountryName
        setUpView()
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        clickListener()
        /* val filter = InputFilter { source, start, end, _, _, _ ->
             for (i in start until end) {
                 if (Character.isWhitespace(source[i])) {
                     return@InputFilter ""
                 }
             }
             null
         }

         edfName?.filters = arrayOf(filter)*/
        edfName.addTextChangedListener(textWatcher)
        edlName.addTextChangedListener(textWatcher2)
    }

    private val textWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val str = s.toString()
            if (str.contains(" ")) {
                edfName.setText(edfName.text.trim())
                edfName.setSelection(edfName.length())
            }
        }

        override
        fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override
        fun afterTextChanged(s: Editable?) {

        }
    }

    private val textWatcher2 = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val str = s.toString()
            if (str.contains(" ")) {
                edlName.setText(edlName.text.trim())
                edlName.setSelection(edlName.length())
            }
        }

        override
        fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override
        fun afterTextChanged(s: Editable?) {

        }
    }

    private var type: String? = null

    private fun setUpView() {
        type = UserType.USER
    }

    private fun clickListener() {
        ivBack.setOnClickListener(this)
        next.setOnClickListener(this)
        // edtBirthDate.setOnClickListener(this)
        edlCountry.setOnClickListener(this)
        if (SharedPrefs.with(this).getString(PREF_LANG, "en") == "en")
            edlCountry?.changeDefaultLanguage(CountryCodePicker.Language.ENGLISH)
        else
            edlCountry?.changeDefaultLanguage(CountryCodePicker.Language.SPANISH)
        llAlreadyMember?.setOnClickListener(this)
        llFacebook.setOnClickListener(this)
        llGoogle.setOnClickListener(this)
        edlCountry.setOnCountryChangeListener {
            countryName = edlCountry.selectedCountryName
        }
        facebookLogin.setFacebookLoginListener(facebookLoginListener)

    }

    private var firstName: String = ""
    private var lastName: String = ""
    private var mDateOfBirth: String = ""
    private var mCountry: String = ""
    private var email: String = ""
    private fun isDataValid(): Boolean {
        firstName = edfName.text.toString().trim()
        lastName = edlName.text.toString().trim()
        //mDateOfBirth = edtBirthDate.text.toString().trim()
        mCountry = countryName
        email = edtEmail.text.toString().trim()
        if (firstName.isEmpty()) {
            edfName.showSnack(getString(R.string.please_enter_firstname))
            edfName.requestFocus()
            return false
        }

        if (lastName.isEmpty()) {
            edlName.showSnack(getString(R.string.please_enter_lastname))
            edlName.requestFocus()
            return false
        }

        if (email.isEmpty()) {
            edtEmail.showSnack(getString(R.string.please_enter_email))
            edtEmail.requestFocus()
            return false
        }

        val value = checkEmailFormat()
        if (!value) {
            edtEmail.showSnack(getString(R.string.email_validation_msg))
            return false
        }
        if (mCountry.isEmpty()) {
            edlCountry.showSnack(getString(R.string.enter_your_country_name))
            edlCountry.requestFocus()
            return false
        }
//        if (mDateOfBirth.isEmpty()) {
//            edtBirthDate.showSnack(getString(R.string.enter_your_date_birth))
//            edtBirthDate.requestFocus()
//            return false
//        }
        return true
    }

    private fun checkEmailFormat(): Boolean {
        val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    @SuppressLint("HardwareIds")
    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }

            R.id.next -> {
                if (isDataValid()) {
                    if (CheckNetworkConnection.isOnline(this)) {
                        if (CheckNetworkConnection.isOnline(this)) {
                            presenter.emailVerificationApiCall(edtEmail.text.toString().trim())
                        } else {
                            CheckNetworkConnection.showNetworkError(contactRoot)
                        }

                    } else {
                        CheckNetworkConnection.showNetworkError(this.signupRoot)
                    }
                }
            }
//            R.id.edtBirthDate -> {
//                openDatePickerForDob(this, this)
//            }
            R.id.llAlreadyMember -> {
                startActivity(Intent(this, SignInActivity::class.java).putExtra(USERTYPE, type))
                finish()
            }
            R.id.llGoogle -> {
                if (CheckNetworkConnection.isOnline(this)) {
                    signInGoogle()
                } else {
                    CheckNetworkConnection.showNetworkError(this.signupRoot)
                }
            }
            R.id.llFacebook -> {
                if (CheckNetworkConnection.isOnline(this)) {
                    facebookLogin.performLogin()
                } else {
                    CheckNetworkConnection.showNetworkError(this.signupRoot)
                }
            }
        }
    }

    override fun apiEmailSuccess(isPhoneNoExists: Boolean) {
        if (!isPhoneNoExists) {
            startActivity(
                Intent(this, ContactInfoActivity::class.java)
                    .putExtra(FIRST_NAME, firstName)
                    .putExtra(LAST_NAME, lastName)
                    .putExtra(USERTYPE, type)
                    .putExtra(DATE_OF_BIRTH, mDateOfBirth)
                    .putExtra(COUNTRY, mCountry)
                    .putExtra(EMAIL, email)
                    .putExtra(PROFILE_PIC, "")
                    .putExtra("key", "")
                    .putExtra(SOCIAL_TYPE, "")
                    .putExtra("requestType", "signup")
            )
        } else {
            signupRoot.showSnack(R.string.email_already_exists)
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

    private val facebookLoginListener = object : FacebookLoginListener {
        override fun onFbLoginSuccess() {
            facebookLogin.getUserProfile()
        }

        override fun onFbLoginCancel() {
            Log.e("fb login", "canceled")
        }

        override fun onFbLoginError(exception: FacebookException?) {
            signupRoot.showSnack(exception!!.localizedMessage)
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
                map[SOCIAL_LOGIN_KEY] = id!!
                map[FIRST_NAME] = firstName!!
                map[LAST_NAME] = lastName!!
                map[PROFILE_PIC_URL] = picUrl!!
                socialType = "Facebook"
                map[SOCIAL_TYPE] = socialType
                if (!email.isNullOrEmpty()) {
                    map[EMAIL] = email
                }
                map[DEVICE_TYPE] = ANDROID
                if (type.equals(UserType.USER)) {
                    map[SOCIAL_LOGIN_TYPE] = UserType.USER
                } else {
                    map[SOCIAL_LOGIN_TYPE] = UserType.DRIVER
                }


                val languageId: String =
                    if (SharedPrefs.with(this@SignUpActivity).getString(PREF_LANG, "en") == "es")
                        "1"
                    else
                        "0"

                map[LANGUAGE_ID] = languageId
                map[DEVICE_TOKEN] =
                    SharedPrefs.with(this@SignUpActivity).getString(DEVICE_TOKEN, "")
                presenter.socialLogin(map)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    override fun apiSuccess(userInfo: SignupModel?) {

    }

    override fun socialLoginApiSuccess(userInfo: SignupModel?) {
        dialogIndeterminate.dismiss()
        if (userInfo?.userExists == true) {
            SharedPrefs.with(this).save(ACCESS_TOKEN, userInfo.accessToken)
            SharedPrefs.with(this).save(USER_ID, userInfo._id)
            SharedPrefs.with(this).save(USER_TYPE, userInfo.type)
            SharedPrefs.with(this).save(USER_DATA, userInfo)
            if (!userInfo.googleId.equals("")) {
                callLogout()
            }
            if (userInfo.type.equals(UserType.USER)) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finishAffinity()

            } else if (userInfo.type.equals(UserType.DRIVER)) {
                val intent = Intent(this, com.capcorp.ui.driver.homescreen.HomeActivity::class.java)
                startActivity(intent)
                finishAffinity()
            } else {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        } else {
            val intent = Intent(this, ContactInfoActivity::class.java)
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
        signupRoot.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        signupRoot.showSnack(errorBody!!)
    }

    override fun validationsFailure(type: String?) {

    }

    private fun signInGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private var profilePic: String = ""
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
            profilePic = account.photoUrl.toString()

            if (account.photoUrl != null) {
                map[PROFILE_PIC_URL] = account.photoUrl.toString()
            }
            socialType = "Google"
            map[SOCIAL_TYPE] = socialType
            map[EMAIL] = account.email!!
            map[DEVICE_TYPE] = ANDROID
            if (type.equals("USER")) {
                map[SOCIAL_LOGIN_TYPE] = "USER"
            } else {
                map[SOCIAL_LOGIN_TYPE] = "DRIVER"
            }


            val languageId: String =
                if (SharedPrefs.with(this@SignUpActivity).getString(PREF_LANG, "en") == "es")
                    "1"
                else
                    "0"

            map[LANGUAGE_ID] = languageId
            map[DEVICE_TOKEN] = SharedPrefs.with(this).getString(DEVICE_TOKEN, "")
            //((activity as SignInActivity)).showPhoneNumberFragment(map)
            presenter.socialLogin(map)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            signupRoot.showSnack(R.string.unbale_to_connect_with_google)
        }

    }


    override fun dateTimeSelected(dateCal: Calendar) {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
//        edtBirthDate.setText(simpleDateFormat.format(dateCal.time))
    }

    fun callLogout() {
        mGoogleSignInClient.signOut()
    }
}