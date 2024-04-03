package com.capcorp.ui.settings.profile.support

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.ui.user.homescreen.account.AccountContract
import com.capcorp.ui.user.homescreen.account.AccountPresenter
import com.capcorp.ui.user.homescreen.home.transport.addpictures.AddPictureAdapter
import com.capcorp.utils.*
import com.capcorp.utils.bottomsheet.BottomSheetMedia
import com.capcorp.webservice.models.*
import kotlinx.android.synthetic.main.activity_support.*
import java.io.File


class SupportActivity : BaseActivity(), AccountContract.View, View.OnClickListener,
    Utility.PassValues, AddPictureAdapter.DeletedImagePosition {
    override fun languageSuccess() {
    }

    override fun blockPush(signupModel: SignupModel) {
    }

    private var userDetail: SignupModel? = null
    private val presenter = AccountPresenter()
    private lateinit var dialogIndeterminate: DialogIndeterminate
    private lateinit var imageAdapter: AddPictureAdapter
    private var mImageList = java.util.ArrayList<ItemImageModel>()
    private var mediaType: String = ""
    private lateinit var utility: Utility
    private var langPickerPopup: androidx.appcompat.widget.ListPopupWindow? = null
    private var mReasonIdList = java.util.ArrayList<String>()
    private var mReasonNameList = java.util.ArrayList<String>()
    private var reasonId: String = ""
    private var reasonName: String = ""
    private var managePermissions: ManagePermissions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)
        presenter.attachView(this)
        dialogIndeterminate = DialogIndeterminate(this)
        managePermissions = ManagePermissions(this)
        setViews()
        setListener()
        imageAdapter()
        presenter.reasonApi(
            getAuthAccessToken(this),
            SharedPrefs.with(this).getString(USER_TYPE, "")
        )
    }

    fun setListener() {
        tvBackSupport.setOnClickListener(this)
        tvGetStarted.setOnClickListener(this)
        tv_reasons.setOnClickListener(this)
    }

    fun setViews() {
        userDetail = SharedPrefs.with(this).getObject(USER_DATA, SignupModel::class.java)

        tv_name.text = userDetail?.fullName

        tv_email.text = userDetail?.emailId

        tv_number.text = userDetail?.fullNumber
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvBackSupport -> {
                finish()
            }
            R.id.tvGetStarted -> {
                if (reasonId.equals("")) {
                    rootView_support?.showSnack(getString(R.string.select_reason))
                } else {
                    /*if (edt_details.text.length > 0) {
                        if (CheckNetworkConnection.isOnline(this)) {
                            var supportRequest = SupportRequest()
                            supportRequest.name = userDetail?.fullName?:""
                            supportRequest.email = userDetail?.emailId?:""
                            supportRequest.fullNumber = userDetail?.fullNumber?:""
                            supportRequest.description = edt_details.text.toString()
                            supportRequest.reasonId = reasonId
                            mImageList.removeAt(mImageList.size - 1)
                            supportRequest.reasonPicUrl.clear()
                            supportRequest.reasonPicUrl.addAll(mImageList)
                            presenter.supportRequest(getAuthAccessToken(this),supportRequest)
                        }
                    }else{
                        rootView_support?.showSnack(getString(R.string.enter_description))
                    }*/
                    if (CheckNetworkConnection.isOnline(this)) {
                        var supportRequest = SupportRequest()
                        supportRequest.name = userDetail?.fullName ?: ""
                        supportRequest.email = userDetail?.emailId ?: ""
                        supportRequest.fullNumber = userDetail?.fullNumber ?: ""
                        supportRequest.description = edt_details.text.toString()
                        supportRequest.reasonId = reasonId
                        mImageList.removeAt(mImageList.size - 1)
                        supportRequest.reasonPicUrl.clear()
                        supportRequest.reasonPicUrl.addAll(mImageList)
                        supportRequest.refundIssue = "false"
                        presenter.supportRequest(getAuthAccessToken(this), supportRequest)
                    }
                }
            }
            R.id.tv_reasons -> {
                openReasonPicker()
            }
        }
    }

    override fun switchUserSuccess(signupModel: SignupModel) {
    }

    override fun onEditProfileApiSuccess(data: SignupModel?) {
    }

    override fun onFaqSuccess(data: List<Faq>) {
    }

    override fun logoutSuccess() {
    }

    override fun reasonSuccess(data: List<Reason>) {
        for (reason in data.indices) {
            if (SharedPrefs.with(this).getString(PREF_LANG, "en").equals("en")) {
                data.get(reason).reasonEn?.let { mReasonNameList.add(it) }
            } else {
                data.get(reason).reasonEs?.let { mReasonNameList.add(it) }
            }
            data.get(reason)._id?.let { mReasonIdList.add(it) }
        }
        initLanguagePicker(tv_reasons)
    }

    override fun supportSuccess() {
        val dialog = Dialog(this,R.style.DialogStyle)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.alert_dialog_success)
        val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
        val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
        val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
        tvTitle.text = getString(R.string.success)
        tvDescription.text = getString(R.string.thanks_for_response_admin)
        dialogButton.text = getString(R.string.ok)
        dialogButton.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }

    override fun showLoader(isLoading: Boolean) {
        dialogIndeterminate.show(isLoading)
    }

    override fun apiFailure() {
        rootView_support?.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
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
            rootView_support?.showSnack(errorBody!!)
        }
    }

    override fun validationsFailure(type: String?) {
    }

    private fun imageAdapter() {
        mImageList.add(ItemImageModel("", ""))     // For initial add image card
        imageAdapter = AddPictureAdapter(adapterCallback, this, rvItemOrder.context, mImageList, 3)
        rvItemOrder.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvItemOrder.isNestedScrollingEnabled = false
        rvItemOrder.adapter = imageAdapter
    }

    private val adapterCallback = object : AddPictureAdapter.AdapterCallback {
        override fun onAddImageClicked() {
            if (mImageList.size < 4) {
                permissionSafetyMethod()
            }
        }
    }

    private fun permissionSafetyMethod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (managePermissions!!.checkPermissions()) mediaOption()
        } else mediaOption()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        utility.onActivityResult(requestCode, resultCode, data)
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
        bottomSheetMedia.show((this as FragmentActivity).supportFragmentManager, "camera")
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

    /* override fun passImageURI(file: File?, photoURI: Uri?) {
         val atIndex = mImageList.size - 1
         val key = System.currentTimeMillis().toString()
         val preGeneratedUrl = S3_BUCKET + System.currentTimeMillis().toString()
         val transferId =   amazonMobile?.uploadFile(key, file!!, object : TransferListener {
             override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {

             }

             override fun onStateChanged(id: Int, state: TransferState?) {
                 if (state == TransferState.COMPLETED) {

                 }
             }

             override fun onError(id: Int, ex: Exception?) {

             }
         })
         //mImageList.add(atIndex, ItemImageModel(file?.toString(), file?.toString(), file?.absolutePath))
         mImageList.add(atIndex, ItemImageModel(preGeneratedUrl, file?.toString(), file?.absolutePath))
         imageAdapter.notifyDataSetChanged()
     }
 */

    override fun passImageURI(file: File?, photoURI: Uri?) {
        val atIndex = mImageList.size - 1
        val key = System.currentTimeMillis().toString()
        val preGeneratedUrl = "$S3_BUCKET$key"
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
        mImageList.add(atIndex, ItemImageModel(preGeneratedUrl, preGeneratedUrl, file.absolutePath))
        imageAdapter.notifyDataSetChanged()
    }


    override fun deletedImagePos(pos: Int) {
        mImageList.removeAt(pos)
        imageAdapter.notifyDataSetChanged()
    }

    private fun openReasonPicker() {
        langPickerPopup?.show()
    }

    private fun initLanguagePicker(anchorView: View) {
        langPickerPopup = androidx.appcompat.widget.ListPopupWindow(anchorView.context)
        langPickerPopup?.setAdapter(
            ArrayAdapter(
                this,
                R.layout.support_simple_spinner_dropdown_item, mReasonNameList
            )
        )
        langPickerPopup?.isModal = true
        langPickerPopup?.anchorView = anchorView
        langPickerPopup?.width = androidx.appcompat.widget.ListPopupWindow.WRAP_CONTENT
        langPickerPopup?.height = androidx.appcompat.widget.ListPopupWindow.WRAP_CONTENT

        langPickerPopup?.setOnItemClickListener { _, _, position, _ ->
            reasonName = mReasonNameList.get(position)
            reasonId = mReasonIdList.get(position)
            tv_reasons.text = reasonName
            langPickerPopup?.dismiss()
        }
    }

}
