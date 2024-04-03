package com.capcorp.ui.settings.profile.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.driver.homescreen.home.ImageViewerActivity
import com.capcorp.ui.settings.profile.otheruserProfile.OtherUserProfileContract
import com.capcorp.ui.settings.profile.otheruserProfile.OtherUserProfilePresenter
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.ui.user_signup.upload_documents.UploadDocumentsActivity
import com.capcorp.ui.user_signup.verification_code.VerificationCodeActivity
import com.capcorp.utils.*
import com.capcorp.utils.bottomsheet.BottomSheetMedia
import com.capcorp.webservice.models.ProfilePicUr
import com.capcorp.webservice.models.SignupModel
import com.capcorp.webservice.models.image_upload.ImageUploadModel
import com.google.gson.Gson
import com.hbb20.CountryCodePicker
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.File

class ProfileActivity : BaseActivity(), ProfileContract.View, View.OnClickListener,
    Utility.PassValues, OtherUserProfileContract.View {

    private val presenter = ProfilePresenter()
    private var mediaType: String = ""
    private lateinit var utility: Utility
    private var preGeneratedUrl = ""
    private var mIsVerified = false
    private lateinit var dialogIndeterminate: DialogIndeterminate
    private var managePermissions: ManagePermissions? = null
    private lateinit var userFile: File
    private var userPhoto = ""
    private var userData: SignupModel? = null
    private var userProfileDataMap: Map<String, String>? = null
    private var enteredEmail: String? = null
    private var enteredNumber: String? = null
    private var enteredCountryCode: String? = null
    private val otherUserProfilePresenter = OtherUserProfilePresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        presenter.attachView(this)
        otherUserProfilePresenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(this)
        managePermissions = ManagePermissions(this)
        userData = SharedPrefs.with(this).getObject(USER_DATA, SignupModel::class.java)

        setListeners()
        etFirstName.addTextChangedListener(textWatcher)
        etLastName.addTextChangedListener(textWatcher2)
        edtBio.addTextChangedListener(textWatcher3)
        getProfile()

    }
    fun getProfile() {
        if (CheckNetworkConnection.isOnline(this)) {
            userData?.type?.let {
                userData?._id?.let { it2 ->
                    otherUserProfilePresenter.onOtherUserProfile(
                        getAuthAccessToken(this), it2,
                        it
                    )
                }

            }
        }
    }
    private val textWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val str = s.toString()
            if (str.contains(" ")) {
                etFirstName.setText(etFirstName.text.trim())
                etFirstName.setSelection(etFirstName.length())
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
                etLastName.setText(etLastName.text.trim())
                etLastName.setSelection(etLastName.length())
            }
        }

        override
        fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override
        fun afterTextChanged(s: Editable?) {

        }
    }

    private val textWatcher3 = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val str = s.toString()
            tvCount.text = str.length.toString() + "/150"
        }

        override
        fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override
        fun afterTextChanged(s: Editable?) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    @SuppressLint("SetTextI18n")
    private fun setProfileData(userData: SignupModel?) {

        mIsVerified = userData?.isApproved == 1
        etFirstName.setText(userData?.firstName)
        etLastName.setText(userData?.lastName)

        userData?.bio.toString().trim().let {
            edtBio.setText(it)
            tvCount.text = it.trim().length.toString() + "/150"
        }

        userData?.documentUploaded?.let {

            if (it) {
                tv_identity_verification?.isEnabled = false
                tv_identity_verification.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_disable_gray)
            }
        }

        if (SharedPrefs.with(this).getString(PREF_LANG, "en") == "en")
            ccp?.changeDefaultLanguage(CountryCodePicker.Language.ENGLISH)
        else
            ccp?.changeDefaultLanguage(CountryCodePicker.Language.SPANISH)

        /*userData?.isApproved?.let {

            if (it == 1) {
                tv_identity_verification?.visibility = View.GONE
                etEmail.isEnabled = true
                tvPhoneNo.isEnabled = true
                ccp?.setCcpClickable(true)

            } else {
                tv_identity_verification?.visibility = View.VISIBLE
                etEmail.isEnabled = false
                tvPhoneNo.isEnabled = false
                ccp?.setCcpClickable(false)
            }

        }*/

        etEmail.setText(userData?.emailId)
        ccp?.setCountryForNameCode(userData?.countryISO)
        tvPhoneNo.setText(userData?.phoneNo)
        userPhoto = userData?.profilePicURL?.original.toString()
        Glide.with(this).load(userPhoto)
            .apply(RequestOptions().placeholder(R.drawable.profile_pic_placeholder).circleCrop())
            .into(ivProfilePic)
        SharedPrefs.with(this).save(PHONE_NUMBER, userData?.phoneNo)

    }

    private fun setListeners() {
        tvSave.setOnClickListener(this)
        fabProfilePic.setOnClickListener(this)
        tvBack.setOnClickListener(this)
        tv_identity_verification.setOnClickListener(this)
        //  tv_connect_stripe.setOnClickListener(this)

        ivProfilePic.setOnClickListener {
            val intent = Intent(applicationContext, ImageViewerActivity::class.java)
            intent.putExtra(PROFILE_PIC_URL, userPhoto)
            startActivity(intent)
        }
    }


/*
    map[PROFILE_PIC_URL] = Gson().toJson(ProfilePicUrl(preGeneratedUrl, preGeneratedUrl))
*/


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvBack -> {
                onBackPressed()
            }
            R.id.tvSave -> {
                if (CheckNetworkConnection.isOnline(this)) {

                    if (this::userFile.isInitialized) {
                        presenter.imageUploadCall(userFile)

                    } else {

                        val map = HashMap<String, String>()

                        if (etFirstName.text.toString().trim() != userData?.firstName)
                            map[FIRST_NAME] = etFirstName.text.toString().trim()

                        if (etLastName.text.toString().trim() != userData?.lastName)
                            map[LAST_NAME] = etLastName.text.toString().trim()


                        map[EMAIL] = etEmail.text.toString().trim()
                        map[COUNTRY_CODE] = ccp?.selectedCountryCodeWithPlus!!
                        map[COUNTRY_ISO] = ccp?.selectedCountryNameCode!!

                        if (preGeneratedUrl.isNotEmpty()) {
                            map[PROFILE_PIC_URL] =
                                Gson().toJson(ProfilePicUr(preGeneratedUrl, preGeneratedUrl))
                        }

                        map[PHONE_NUMBER] = tvPhoneNo.text.toString().trim()
                        map[EMAIL] = etEmail.text.toString().trim()
                        map[BIO] = edtBio.text.toString().trim()
                        presenter.checkValidations(map)

                    }

                } else {
                    CheckNetworkConnection.showNetworkError(rootView)
                }
            }
            R.id.fabProfilePic -> {

                if (CheckNetworkConnection.isOnline(this)) {
                    permissionSafetyMethod()
                } else {
                    CheckNetworkConnection.showNetworkError(rootView)
                }

            }
            R.id.tv_identity_verification -> {
                val intent = Intent(this, UploadDocumentsActivity::class.java)
                intent.putExtra("from_screen_type", "editprofile")
                startActivity(intent)
            }
        }
    }

    override fun onValidationsSuccess(map: Map<String, String>) {


        enteredEmail = map.getValue(EMAIL)
        enteredNumber = map.getValue(PHONE_NUMBER)
        enteredCountryCode = map.getValue(COUNTRY_CODE)

        userProfileDataMap = map

        if (userData?.phoneNo != enteredNumber || userData?.countryCode != enteredCountryCode) {
            presenter.phoneVerificationApiCall(
                enteredCountryCode + enteredNumber,
                userData?.type!!,
                "phoneUpdate"
            )
        } else if (userData?.emailId != enteredEmail) {
            presenter.emailVerificationApiCall(enteredEmail!!)
        } else
            presenter.editProfileApiCall(getAuthAccessToken(this), map)
    }

    override fun apiEmailSuccess(_isEmailExists: Boolean) {

        if (!_isEmailExists) {

            Intent(this, VerificationCodeActivity::class.java).also {

                it.putExtra("requestType", "VerificationNumberType")
                it.putExtra(COUNTRY_CODE, enteredCountryCode)
                it.putExtra(PHONE_NUMBER, enteredNumber)
                it.putExtra(OLD_PHONE_NUMBER, userData?.countryCode + userData?.phoneNo)
                it.putExtra(COUNTRY_ISO, ccp.selectedCountryNameCode)
                startActivityForResult(it, 777)

            }
        } else
            rootView.showSnack(R.string.email_already_exists)


    }

    override fun apiNumberSuccess(isPhoneNoExists: Boolean) {
        when {
            isPhoneNoExists -> {
                rootView.showSnack(R.string.phone_number_already_exists)
            }
            userData?.emailId != enteredEmail -> {
                presenter.emailVerificationApiCall(enteredEmail!!)
            }
            else -> {

                Intent(this, VerificationCodeActivity::class.java).also {

                    it.putExtra("requestType", "VerificationNumberType")
                    it.putExtra(COUNTRY_CODE, ccp?.selectedCountryCodeWithPlus)
                    it.putExtra(PHONE_NUMBER, enteredNumber)
                    it.putExtra(OLD_PHONE_NUMBER, userData?.countryCode + userData?.phoneNo)
                    it.putExtra(COUNTRY_ISO, ccp.selectedCountryNameCode)
                    startActivityForResult(it, 777)

                }

            }
        }
    }


    override fun onEditProfileApiSuccess(data: SignupModel?) {
        SharedPrefs.with(this).save(USER_DATA, data)
        Toast.makeText(this, getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()


        setResult(Activity.RESULT_OK, Intent())
        finish()


    }

    override fun onUploadImageSuccess(uploadModel: ImageUploadModel) {


        preGeneratedUrl = uploadModel.fileData?.original.toString()

        val map = HashMap<String, String>()


        if (etFirstName.text.toString().trim() != userData?.firstName)
            map[FIRST_NAME] = etFirstName.text.toString().trim()

        if (etLastName.text.toString().trim() != userData?.lastName)
            map[LAST_NAME] = etLastName.text.toString().trim()


        map[PHONE_NUMBER] = tvPhoneNo.text.toString().trim()
        map[EMAIL] = etEmail.text.toString().trim()
        map[BIO] = edtBio.text.toString().toString()
        map[COUNTRY_CODE] = ccp?.selectedCountryCodeWithPlus!!
        map[COUNTRY_ISO] = ccp?.selectedCountryNameCode!!

        if (preGeneratedUrl.isNotEmpty()) {
            map[PROFILE_PIC_URL] = Gson().toJson(ProfilePicUr(preGeneratedUrl, preGeneratedUrl))
        }

        presenter.checkValidations(map)
    }

    override fun onOtherUserProfile(data: SignupModel?) {
        SharedPrefs.with(this).save(USER_DATA, data)
        userData = data
        setProfileData(data)
    }

    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun apiFailure() {
        dialogIndeterminate.dismiss()
        rootView.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        dialogIndeterminate.dismiss()
        if (code == 401) {
            val dialog = Dialog(this,R.style.DialogStyle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.alert_dialog)
            val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
            val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
            val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
            tvTitle.text = getString(R.string.error)
            tvDescription.text = getString(R.string.sorry_account_have_been_logged)
            dialogButton.text = getString(R.string.ok)
            dialogButton.setOnClickListener {
                dialog.dismiss()
                SharedPrefs.with(this).remove(ACCESS_TOKEN)
                finishAffinity()
                startActivity(Intent(this, SplashActivity::class.java))
            }
            dialog.show()
        } else {
            rootView.showSnack(errorBody ?: getString(R.string.sww_error))
        }
    }

    override fun validationsFailure(type: String?) {
        when (type) {
            Validations.FIRST_NAME_EMPTY -> {
                rootView.showSnack(R.string.please_enter_firstname)
            }
            Validations.LAST_NAME_EMPTY -> {
                rootView.showSnack(R.string.please_enter_lastname)
            }

            Validations.NUMBER_EMPTY -> {
                rootView.showSnack(R.string.please_enter_number)
            }

            Validations.NUMBER_INVALID -> {
                rootView.showSnack(R.string.please_enter_valid_number)
            }

            Validations.EMAIL_EMPTY -> {
                rootView.showSnack(R.string.please_enter_email)
            }

            Validations.EMAIL_INVALID -> {
                rootView.showSnack(R.string.please_enter_valid_email)
            }
        }
    }

    private fun mediaOption() {
        val bottomSheetMedia = BottomSheetMedia()
        bottomSheetMedia.setOnCameraListener(View.OnClickListener {
            openCamera()
            bottomSheetMedia.dismiss()
        })

        bottomSheetMedia.setOnGalleryListener(View.OnClickListener {
            openGallery()
            bottomSheetMedia.dismiss()

        })

        bottomSheetMedia.setOnCancelListener(View.OnClickListener {
            bottomSheetMedia.dismiss()
        })
        bottomSheetMedia.show(supportFragmentManager, "camera")
    }

    fun openGallery() {
        mediaType = UtilityConstants.GALLERY
        utility = Utility(this, UtilityConstants.GALLERY)
        utility.selectImage()
    }

    fun openCamera() {
        mediaType = UtilityConstants.CAMERA
        utility = Utility(this, UtilityConstants.CAMERA)
        utility.selectImage()
    }

    override fun passImageURI(file: File?, photoURI: Uri?) {
        if (file != null) {


            val uri = Uri.fromFile(file)

            Glide.with(this).load(uri)
                .apply(
                    RequestOptions().placeholder(R.drawable.profile_pic_placeholder).circleCrop()
                )
                .into(ivProfilePic)

            userFile = file
        }
    }

    private fun permissionSafetyMethod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (managePermissions!!.checkPermissions()) mediaOption()
        } else mediaOption()
    }

    override fun onResume() {
        super.onResume()
        Log.e("cvbcb", "onResume is called")

    }

    override fun onPause() {
        super.onPause()
        Log.e("cvbcb", "onPause is called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.e("cvbcb", "onRestart is called")

    }

    override fun onStop() {
        super.onStop()
        Log.e("cvbcb", "onStop is called")
    }

    override fun onStart() {
        super.onStart()
        Log.e("cvbcb", "onStart is called")

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 777) {
            if (resultCode == Activity.RESULT_OK) {
                presenter.editProfileApiCall(getAuthAccessToken(this), userProfileDataMap!!)
            }

            if (resultCode == Activity.RESULT_CANCELED) {
                showToast(getString(R.string.verification_error))
            }


        } else {

            if (utility != null) {
                utility.onActivityResult(requestCode, resultCode, data)
            }
        }

    }


}
