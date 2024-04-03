package com.capcorp.ui.user_signup.createPassword

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.signup.phone_verification.CreatePasswordContract
import com.capcorp.ui.signup.phone_verification.CreatePasswordPresenter
import com.capcorp.ui.user.homescreen.HomeActivity
import com.capcorp.ui.user_signup.upload_image.UploadImageActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.SignupModel
import kotlinx.android.synthetic.main.activity_create_password.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class CreatePasswordActivity : BaseActivity(), CreatePasswordContract.View {
    override fun onDocumentUploadedSuccess(data: SignupModel?) {
    }

    private val presenter = CreatePasswordPresenter()

    private var showPassword = false

    private lateinit var dialogIndeterminate: DialogIndeterminate

    private var firstName: String = ""
    private var lastName: String = ""
    private var mDateOfBirth: String = ""
    private var mCountry: String = ""
    private var type: String = ""
    private var email: String = ""
    private var countryCode: String = ""
    private var countryIso: String = ""
    private var phoneNumber: String = ""

    //private var mFilePath: String = ""
    private var mUserType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_password)
        presenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(this)
        getData()
        setListeners()
    }

    private fun getData() {
        firstName = intent.getStringExtra(FIRST_NAME).toString()
        lastName = intent.getStringExtra(LAST_NAME).toString()
        mDateOfBirth = intent.getStringExtra(DATE_OF_BIRTH).toString()
        type = intent.getStringExtra(USERTYPE).toString()
        mCountry = intent.getStringExtra(COUNTRY).toString()
        email = intent.getStringExtra(EMAIL).toString()
        countryIso = intent.getStringExtra(COUNTRY_ISO).toString()
        countryCode = intent.extras!!.getString(COUNTRY_CODE)!!
        phoneNumber = intent.extras!!.getString(PHONE_NUMBER)!!
        //mFilePath = intent.getStringExtra(FILEPATH)
        /*if (type.equals("SHOPPER")){
            mUserType = "USER"
        }else{
            mUserType = "DRIVER"
        }*/
    }

    private fun setListeners() {
        tvShow.setOnClickListener { showPassword() }
        ivBack_password.setOnClickListener { finish() }
        fab_next.setOnClickListener(fabClickListener)
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

    private var password: String = ""
    private fun isValid(): Boolean {
        password = etPassword.text.toString().trim()
        if (password.isEmpty()) {
            etPassword.showSnack(getString(R.string.please_enter_password))
            etPassword.requestFocus()
            return false
        } else if ((password.length <= 5)) {
            etPassword.showSnack(R.string.enter_valid_password)
            etPassword.requestFocus()
            return false
        }
        return true
    }

    private val fabClickListener = View.OnClickListener {
        /*var newfile: File? = null
        var imageFileBody: MultipartBody.Part? = null
        if (mFilePath != null && mFilePath != "") {
            newfile = File(mFilePath)
        }
        if (newfile != null) {
            val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), newfile)
            imageFileBody = MultipartBody.Part.createFormData("profilePic", newfile.name, requestBody)
        }*/
        if (isValid()) {
            if (type.equals("USER")) {
                val countryCode = countryCode.toRequestBody("text/plain".toMediaTypeOrNull())
                val fullnumber = phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull())
                val firstName = firstName.toRequestBody("text/plain".toMediaTypeOrNull())
                val lastName = lastName.toRequestBody("text/plain".toMediaTypeOrNull())
                val email = email.toRequestBody("text/plain".toMediaTypeOrNull())
                val lat = "0".toRequestBody("text/plain".toMediaTypeOrNull())
                val lng = "0".toRequestBody("text/plain".toMediaTypeOrNull())
                val default_lat = "0".toRequestBody("text/plain".toMediaTypeOrNull())
                val default_lng = "0".toRequestBody("text/plain".toMediaTypeOrNull())
                val password = etPassword.text.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull())
                val deviceType = ANDROID.toRequestBody("text/plain".toMediaTypeOrNull())
                val usertype = "USER".toRequestBody("text/plain".toMediaTypeOrNull())
                val dateOfBirth = mDateOfBirth.toRequestBody("text/plain".toMediaTypeOrNull())
                val deviceToken = SharedPrefs.with(this).getString(DEVICE_TOKEN, "")
                    .toRequestBody("text/plain".toMediaTypeOrNull())
                val country = mCountry.toRequestBody("text/plain".toMediaTypeOrNull())
                val countryIsoBody = countryIso
                    .toRequestBody("text/plain".toMediaTypeOrNull())


                val language: String =
                    if (SharedPrefs.with(this).getString(PREF_LANG, "en") == "es")
                        "1"
                    else
                        "0"

                val languageId = language.toRequestBody("text/plain".toMediaTypeOrNull())



                presenter.shopperSignup(
                    countryCode,
                    fullnumber,
                    firstName,
                    lastName,
                    email,
                    lat,
                    lng,
                    default_lat,
                    default_lng,
                    password,
                    deviceType,
                    usertype,
                    dateOfBirth,
                    deviceToken,
                    country,
                    languageId,
                    countryIsoBody
                )
            } else {
                val intent = Intent(applicationContext, UploadImageActivity::class.java)
                intent.putExtra(COUNTRY_CODE, countryCode)
                intent.putExtra(PHONE_NUMBER, phoneNumber)
                intent.putExtra(FIRST_NAME, firstName)
                intent.putExtra(LAST_NAME, lastName)
                intent.putExtra(DATE_OF_BIRTH, mDateOfBirth)
                intent.putExtra(USERTYPE, type)
                intent.putExtra(COUNTRY, mCountry)
                intent.putExtra(EMAIL, email)
                intent.putExtra(COUNTRY_ISO, countryIso)
                intent.putExtra(PASSWORD, etPassword.text.toString().trim())
                startActivity(intent)
            }
        }

    }

    override fun onSignupSuccess(data: SignupModel?) {


    }

    override fun onTravellerSignUpSuccess(data: SignupModel?) {
        dialogIndeterminate.dismiss()
        SharedPrefs.with(this).save(ACCESS_TOKEN, data?.accessToken)
        SharedPrefs.with(this).save(USER_ID, data?._id)
        SharedPrefs.with(this).save(USER_TYPE, data?.type)
        SharedPrefs.with(this).save(USER_DATA, data)
//        SharedPrefs.with(this).save(IS_MUTE, data?.isMute)
        /*if (data?.type.equals(UserType.USER)) {
            val intent = Intent(this, ThanksTravellerActivity::class.java)
            intent.putExtra("type", data?.type)
            startActivity(intent)
            finishAffinity()
        } else if (data?.type.equals(UserType.DRIVER)) {
            val intent = Intent(this, ThanksTravellerActivity::class.java)
            intent.putExtra("type", data?.type)
            startActivity(intent)
            finishAffinity()
        } else {
            val intent = Intent(this, ThanksTravellerActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }*/

        if (data?.type.equals(UserType.USER)) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finishAffinity()
        } else {
            val intent =
                Intent(this, com.capcorp.ui.driver.homescreen.HomeActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

    }

    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun showMessage(message: String): String {
        createPasswordRoot.showSWWerror(message)
        return super.showMessage(message)
    }

    override fun apiFailure() {
        dialogIndeterminate.dismiss()
        createPasswordRoot.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        dialogIndeterminate.dismiss()
        createPasswordRoot.showSnack(errorBody!!)
    }

    override fun validationsFailure(type: String?) {
        when (type) {
            Validations.FIELD_EMPTY -> {
                createPasswordRoot.showSnack(R.string.please_enter_password)
            }
            Validations.FIELD_INVALID -> {
                createPasswordRoot.showSnack(R.string.password_length_validation_msg)
            }
        }
    }
}
