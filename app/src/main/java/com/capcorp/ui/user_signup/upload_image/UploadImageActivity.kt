package com.capcorp.ui.user_signup.upload_image

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.signup.phone_verification.CreatePasswordContract
import com.capcorp.ui.signup.phone_verification.CreatePasswordPresenter
import com.capcorp.ui.user.homescreen.HomeActivity
import com.capcorp.utils.*
import com.capcorp.utils.bottomsheet.BottomSheetMedia
import com.capcorp.webservice.models.ProfilePicUr
import com.capcorp.webservice.models.SignUpModelRequest
import com.capcorp.webservice.models.SignupModel
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_upload_image.*
import java.io.File


class UploadImageActivity : BaseActivity(), Utility.PassValues, View.OnClickListener,
    CreatePasswordContract.View {

    private var mediaType: String = ""
    private lateinit var utility: Utility
    private val permissionCode = 209
    private var preGeneratedUrl = ""
    private val permission =
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    private val presenter = CreatePasswordPresenter()
    private var managePermissions: ManagePermissions? = null
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
    private var mFilePath: String = ""
    private var mPassword: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)
        presenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(this)
        managePermissions = ManagePermissions(this)
        getData()
        setListener()
    }

    fun getData() {
        firstName = intent.getStringExtra(FIRST_NAME).toString()
        lastName = intent.getStringExtra(LAST_NAME).toString()
        mDateOfBirth = intent.getStringExtra(DATE_OF_BIRTH).toString()
        type = intent.getStringExtra(USERTYPE).toString()
        mCountry = intent.getStringExtra(COUNTRY).toString()
        email = intent.getStringExtra(EMAIL).toString()
        countryIso = intent.getStringExtra(COUNTRY_ISO).toString()
        mPassword = intent.getStringExtra(PASSWORD).toString()
        countryCode = intent.extras!!.getString(COUNTRY_CODE)!!
        phoneNumber = intent.extras!!.getString(PHONE_NUMBER)!!
    }

    fun setListener() {
        ivBackVerify.setOnClickListener(this)
        view_upload_pic.setOnClickListener(this)
        fab_upload_image.setOnClickListener(this)
        iv_delete_image.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackVerify -> {
                onBackPressed()
            }

            R.id.view_upload_pic -> {
                if (CheckNetworkConnection.isOnline(this)) {
                    permissionSafetyMethod()
                } else {
                    CheckNetworkConnection.showNetworkError(rootView)
                }
            }

            R.id.iv_delete_image -> {
                mFilePath = ""
                view_uploaded.visibility = View.GONE
                tv_upload_pic.visibility = View.VISIBLE
                ivProfileImage.visibility = View.GONE

            }

            R.id.fab_upload_image -> {
                if (mFilePath.equals("")) {
                    Toast.makeText(
                        this,
                        getString(R.string.please_upload_your_pic),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val signUpModelRequest = SignUpModelRequest()
                    signUpModelRequest.countryCode = countryCode
                    signUpModelRequest.country = mCountry
                    signUpModelRequest.fullNumber = phoneNumber
                    signUpModelRequest.firstName = firstName
                    signUpModelRequest.lastName = lastName
                    signUpModelRequest.emailId = email
                    signUpModelRequest.lat = "0"
                    signUpModelRequest.long = "0"
                    signUpModelRequest.defaultLat = "0"
                    signUpModelRequest.defaultLong = "0"
                    signUpModelRequest.password = mPassword
                    signUpModelRequest.deviceType = ANDROID
                    signUpModelRequest.deviceToken =
                        SharedPrefs.with(this).getString(DEVICE_TOKEN, "")
                    signUpModelRequest.type = "DRIVER"
                    signUpModelRequest.deviceType = ANDROID
                    signUpModelRequest.dateOfBirth = mDateOfBirth
                    signUpModelRequest.deviceType = ANDROID
                    signUpModelRequest.countryISO = countryIso
                    val languageId: String =
                        if (SharedPrefs.with(this).getString(PREF_LANG, "en") == "es")
                            "1"
                        else
                            "0"
                    signUpModelRequest.languageId = languageId


                    var profilePic = ProfilePicUr()
                    profilePic.original = preGeneratedUrl
                    profilePic.thumbnail = preGeneratedUrl
                    signUpModelRequest.profilePic = profilePic
                    presenter.travellerSignup(signUpModelRequest)
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        showLoader(false)
        presenter.detachView()
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

    private fun openGallery() {
        mediaType = UtilityConstants.GALLERY
        utility = Utility(this, UtilityConstants.GALLERY)
        utility.selectImage()
    }

    private fun openCamera() {
        mediaType = UtilityConstants.CAMERA
        utility = Utility(this, UtilityConstants.CAMERA)
        utility.selectImage()
    }

    override fun passImageURI(file: File?, photoURI: Uri?) {

        //for local image storage
        if (file != null) {
            mFilePath = file.toString()

            view_uploaded.visibility = View.VISIBLE
            tv_image_name.text = file.name

            val imgFile = File(mFilePath)

            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                ivProfileImage?.setImageBitmap(myBitmap)
                ivProfileImage.visibility = View.VISIBLE

                tv_upload_pic.visibility = View.GONE
            }
        }

        Log.e("image_path", mFilePath + "")

        val key = System.currentTimeMillis().toString()
        preGeneratedUrl = "$S3_BUCKET$key"
        val amazonMobile = AwsMobile(this, key)
        amazonMobile.uploadFile(key, file!!, object : TransferListener {
            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {

            }

            override fun onStateChanged(id: Int, state: TransferState?) {
                if (state == TransferState.COMPLETED) {
                    Thread(Runnable {
                        run {
                            amazonMobile.getS3ClientInstance()
                                .setObjectAcl("h2d-dev", key, CannedAccessControlList.PublicRead)
                        }
                    }).start()
                }
            }

            override fun onError(id: Int, ex: Exception?) {

            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (utility != null) {
            utility.onActivityResult(requestCode, resultCode, data)
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
        /*if (data?.type.equals("USER")) {
            val intent = Intent(this, ThanksTravellerActivity::class.java)
            intent.putExtra("type", data?.type)
            startActivity(intent)
            finishAffinity()
        } else if (data?.type.equals("DRIVER")) {
            val intent = Intent(this, ThanksTravellerActivity::class.java)
            intent.putExtra("type", data?.type)
            startActivity(intent)
            finishAffinity()
        } else {
            val intent = Intent(this, ThanksTravellerActivity::class.java)
            intent.putExtra("type", data?.type)
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

    private fun permissionSafetyMethod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (managePermissions!!.checkPermissions()) mediaOption()
        } else mediaOption()

    }

    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun showMessage(message: String): String {
        uploadRoot.showSWWerror(message)
        return super.showMessage(message)
    }

    override fun apiFailure() {
        dialogIndeterminate.dismiss()
        uploadRoot.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        dialogIndeterminate.dismiss()
        uploadRoot.showSnack(errorBody!!)
    }

    override fun validationsFailure(type: String?) {
        when (type) {
            Validations.FIELD_EMPTY -> {
                uploadRoot.showSnack(R.string.please_enter_password)
            }
            Validations.FIELD_INVALID -> {
                uploadRoot.showSnack(R.string.password_length_validation_msg)
            }
        }
    }

    override fun onDocumentUploadedSuccess(data: SignupModel?) {
    }
}
