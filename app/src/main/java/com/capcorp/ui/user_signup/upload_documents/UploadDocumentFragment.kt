package com.capcorp.ui.user_signup.upload_documents


import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.capcorp.R
import com.capcorp.ui.signup.phone_verification.CreatePasswordContract
import com.capcorp.ui.signup.phone_verification.CreatePasswordPresenter
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.ui.user.homescreen.HomeActivity
import com.capcorp.ui.user.homescreen.home.transport.addpictures.AddPictureAdapter
import com.capcorp.utils.*
import com.capcorp.utils.bottomsheet.BottomSheetMedia
import com.capcorp.webservice.models.ItemImageModel
import com.capcorp.webservice.models.SignupModel
import kotlinx.android.synthetic.main.fragment_upload_document.*
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
class UploadDocumentFragment : Fragment(), Utility.PassValues,
    AddPictureAdapter.DeletedImagePosition, CreatePasswordContract.View {

    private lateinit var activity: UploadDocumentsActivity
    private lateinit var imageAdapter: AddPictureAdapter
    private var mImageList = java.util.ArrayList<ItemImageModel>()
    private var mediaType: String = ""
    private lateinit var utility: Utility
    private val presenter = CreatePasswordPresenter()
    private var screenType: String = ""
    private var managePermissions: ManagePermissions? = null
    private lateinit var dialogIndeterminate: DialogIndeterminate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_document, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)
        activity = getActivity() as UploadDocumentsActivity
        managePermissions = ManagePermissions(activity)
        dialogIndeterminate = DialogIndeterminate(activity)
        utility = Utility(this, activity)
        imageAdapter()
        ivBackVerify.setOnClickListener {
            activity.onBackPressed()
        }
        fab_upload_image.setOnClickListener {
            if (mImageList.size == 1) {
                Toast.makeText(activity, getString(R.string.pick_image_first), Toast.LENGTH_SHORT)
                    .show()
            } else {
                mImageList.removeAt(mImageList.size - 1)
                activity.verifyDocumentRequestModel.documents.clear()
                activity.verifyDocumentRequestModel.documents.addAll(mImageList)
                presenter.verifyDocuments(
                    getAuthAccessToken(activity),
                    activity.verifyDocumentRequestModel
                )
            }
        }
        screenType = UploadDocumentsActivity.screenType
    }

    private fun imageAdapter() {
        mImageList.add(ItemImageModel("", ""))     // For initial add image card
        imageAdapter =
            AddPictureAdapter(adapterCallback, this, rvItemsImages.context, mImageList, 3)
        rvItemsImages.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rvItemsImages.isNestedScrollingEnabled = false
        rvItemsImages.adapter = imageAdapter
    }

    private val adapterCallback = object : AddPictureAdapter.AdapterCallback {
        override fun onAddImageClicked() {
            if (mImageList.size < 4) {
                permissionSafetyMethod()
            }
        }
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
        bottomSheetMedia.show((activity as FragmentActivity).supportFragmentManager, "camera")
    }

    private fun openGallery() {
        mediaType = UtilityConstants.GALLERY
        utility = Utility(this@UploadDocumentFragment, activity, UtilityConstants.GALLERY)
        utility.selectImage()
    }

    private fun openCamera() {
        mediaType = UtilityConstants.CAMERA
        utility = Utility(this@UploadDocumentFragment, activity, UtilityConstants.CAMERA)
        utility.selectImage()
    }

    override fun passImageURI(file: File?, photoURI: Uri?) {
        val atIndex = mImageList.size - 1
        val key = System.currentTimeMillis().toString()
        val preGeneratedUrl = "$S3_BUCKET$key"
        val amazonMobile = AwsMobile(activity, key)
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
        /* mImageList.removeAt(pos)
         imageAdapter.notifyItemRemoved(pos)
         imageAdapter.notifyItemRangeChanged(pos, mImageList.size-1)*/
        mImageList.removeAt(pos)
        imageAdapter.notifyDataSetChanged()
    }

    override fun onSignupSuccess(data: SignupModel?) {

    }

    override fun onTravellerSignUpSuccess(data: SignupModel?) {
    }

    override fun onDocumentUploadedSuccess(data: SignupModel?) {
        SharedPrefs.with(activity).save(ACCESS_TOKEN, data?.accessToken)
        SharedPrefs.with(activity).save(USER_ID, data?._id)
        SharedPrefs.with(activity).save(USER_TYPE, data?.type)
        SharedPrefs.with(activity).save(USER_DATA, data)
        if (screenType.equals("signup_process")) {
            val dialog = Dialog(requireActivity(),R.style.DialogStyle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.alert_dialog_success)
            val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
            val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
            val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
            tvTitle.text = getString(R.string.success)
            tvDescription.text = getString(R.string.documents_are_successfully_uploaded)
            dialogButton.text = getString(R.string.ok)
            dialogButton.setOnClickListener {
                dialog.dismiss()
                if (data?.type.equals(UserType.USER)) {
                    val intent = Intent(activity, HomeActivity::class.java)
                    startActivity(intent)
                    activity.finishAffinity()
                } else if (data?.type.equals(UserType.DRIVER)) {
                    val intent =
                        Intent(activity, com.capcorp.ui.driver.homescreen.HomeActivity::class.java)
                    startActivity(intent)
                    activity.finishAffinity()
                } else {
                    val intent = Intent(activity, HomeActivity::class.java)
                    startActivity(intent)
                    activity.finishAffinity()
                }
            }
            dialog.show()
        } else {
            val dialog = Dialog(requireActivity(),R.style.DialogStyle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.alert_dialog_success)
            val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
            val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
            val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
            tvTitle.text = getString(R.string.success)
            tvDescription.text = getString(R.string.documents_are_successfully_uploaded)
            dialogButton.text = getString(R.string.ok)
            dialogButton.setOnClickListener {
                dialog.dismiss()
                activity.finish()
            }
            dialog.show()
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

    override fun apiFailure() {
        view_parent.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        view_parent.showSnack(errorBody ?: getString(R.string.sww_error))
    }

    override fun validationsFailure(type: String?) {
    }

}
