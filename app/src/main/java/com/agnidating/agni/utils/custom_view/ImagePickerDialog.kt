package com.agnidating.agni.utils.custom_view

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.agnidating.agni.R
import com.agnidating.agni.databinding.DialogImagePickerBinding
import com.agnidating.agni.utils.FileUtils
import com.agnidating.agni.utils.showToast
import com.agnidating.agni.utils.toPx
import java.io.File


class ImagePickerDialog(private val mContext: Context, val listener: FileSelectListener) :
    DialogFragment(), View.OnClickListener {
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var launcher: ActivityResultLauncher<Array<String>>
    private lateinit var galleryPermission: ActivityResultLauncher<Array<String>>
    private lateinit var binding: DialogImagePickerBinding
    private lateinit var file: File
    private lateinit var fileUri: Uri


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_image_picker,
            container,
            false
        )
        binding.onclick = this
        val outputDir =
            mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES) // context being the Activity pointer

        file = File.createTempFile("tempProfile", ".jpeg", outputDir)
        fileUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )
        createLaunchers()
        return binding.root
    }

    private fun createLaunchers() {
        launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            var result = true
            it.entries.forEach {
                if (it.value == false) {
                    result = false
                    onPermissionFailed(false, "${it.key} permission Denied")
                }
            }
            if (result) {
                cameraLauncher.launch(fileUri)
            }
        }
        galleryPermission =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                var result = true
                it.entries.forEach {
                    if (it.value == false) {
                        result = false
                        onPermissionFailed(false, "${it.key} permission Denied")
                    }
                }
                if (result) {
                    galleryLauncher.launch("image/*")
                }
            }
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                dismiss()
                listener.onFileSelected(file)
            }
        }
        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            file = FileUtils.getFile(mContext, it)
            listener.onFileSelected(file)
            dismiss()

        }
    }

    private fun onPermissionFailed(b: Boolean, s: String) {
        requireActivity().showToast(s)
    }

    override fun onStart() {
        super.onStart()
        val displayMetrics = DisplayMetrics()
        (requireContext() as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels - 50f.toPx
        dialog!!.window!!.setLayout(width.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog!!.window!!.setBackgroundDrawableResource(R.color.white_opacity_2)
    }


    override fun onClick(p0: View?) {
        when (p0) {
            binding.tvCamera -> {
                launchCamera()
            }
            binding.tvGallery -> {
                launcGallery()
            }
            binding.tvCancel -> {
                dismiss()
            }
        }
    }

    private fun launcGallery() {
        galleryPermission.launch(
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    private fun launchCamera() {
        launcher.launch(
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    interface FileSelectListener {
        fun onFileSelected(file: File)
    }
}