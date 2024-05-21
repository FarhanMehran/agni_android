package com.agnidating.agni.ui.fragment.report

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.databinding.BtmReportBinding
import com.agnidating.agni.model.home.User
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.adapters.AddReportImages
import com.agnidating.agni.utils.custom_view.ImagePickerDialog
import com.agnidating.agni.utils.showToast
import com.agnidating.agni.utils.toMultipartList
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


/**
 * Create by AJAY ASIJA on 04/22/2022
 */
@AndroidEntryPoint
class ReportBottomSheet(val user: User) :BottomSheetDialogFragment(),
    ImagePickerDialog.FileSelectListener {
    private lateinit var binding:BtmReportBinding
    private val viewModel:ReportBlockViewModel by viewModels()
    private val images=ArrayList<File>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= BtmReportBinding.inflate(layoutInflater)
        initDescText()
        setAdapter()
        listeners()
        bindObserver()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initDescText() {
        binding.tvReport.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP ->
                    v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
    }

    /**
     * set adapter for recyclerview
     */
    private fun setAdapter() {
        binding.rvImages.adapter=AddReportImages(images){
            val dialog= ImagePickerDialog(requireContext(),this)
            dialog.show(parentFragmentManager,"")
        }
    }

    private fun bindObserver() {
        viewModel.baseLiveData.observe(viewLifecycleOwner){
            when (it) {
                is ResultWrapper.Loading -> {
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    requireActivity().showToast(it.data.message ?: "")
                    dismiss()
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    requireActivity().showToast(it.error?.message ?: "")
                }
            }
        }
    }

    private fun listeners() {
        binding.btNext.setOnClickListener {
            if (binding.tvReport.text.isNullOrEmpty().not()){
                val map= HashMap<String,RequestBody>()
                map["note"]=binding.tvReport.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                map["reportId"]=user.id.toRequestBody("text/plain".toMediaTypeOrNull())
                Log.d("user___http", user.id.toString())
                viewModel.reportUser(map,images.toMultipartList("img[]","image/*"))
                Log.d("maps",map.toString())
            }else{
                requireActivity().showToast("Enter description")
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onFileSelected(file: File) {
        viewLifecycleOwner.lifecycleScope.launch {
            val compressedImageFile = Compressor.compress(requireContext(), file){
                default()
            }
            images.add(compressedImageFile)
            withContext(Dispatchers.Main){
                binding.rvImages.adapter?.notifyDataSetChanged()
            }
        }
    }
}