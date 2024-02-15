package com.agnidating.agni.ui.fragment.optionBottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.databinding.BtmChooseOptionsBinding
import com.agnidating.agni.model.home.User
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.fragment.report.ReportBlockViewModel
import com.agnidating.agni.ui.fragment.report.ReportBottomSheet
import com.agnidating.agni.utils.custom_view.ConfirmDialog
import com.agnidating.agni.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


/**
 * Create by AJAY ASIJA on 04/22/2022
 */
@AndroidEntryPoint
class OptionBottomSheet(val user: User) :BottomSheetDialogFragment(){
    private lateinit var binding:BtmChooseOptionsBinding
    private val viewModel: ReportBlockViewModel by viewModels()
    var showUnMatch=false
    var callback: ((String) -> Unit)? =null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= BtmChooseOptionsBinding.inflate(layoutInflater)
        binding.tvUnmatch.isVisible=showUnMatch
        listeners()
        bindObserver()
        return binding.root
    }

    private fun bindObserver() {
        viewModel.baseLiveData.observe(viewLifecycleOwner){
            when (it) {
                is ResultWrapper.Loading -> {
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    callback?.invoke("other")
                    requireActivity().showToast(it.data.message ?: "")
                    dismiss()
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    requireActivity().showToast(it.error?.message ?: "")
                }
            }
        }
        viewModel.blockLiveData.observe(viewLifecycleOwner){
            when (it) {
                is ResultWrapper.Loading -> {
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    callback?.invoke("block")
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
        binding.tvBlock.setOnClickListener {
            val dialog=ConfirmDialog{
                if (it){
                    val map= HashMap<String,String>()
                    map["targetId"]=user.id
                    viewModel.blockUser(map)
                }else{
                    dismiss()
                }
            }
            dialog.title="Block User"
            dialog.desc="Are you sure you want to block this user ?"
            dialog.user=user
            dialog.show(parentFragmentManager,"")
        }
        binding.tvUnmatch.setOnClickListener {
            val dialog=ConfirmDialog{
                if (it){
                    val map= HashMap<String,String>()
                    map["targetId"]=user.id
                    viewModel.unMatch(map)
                }else{
                    dismiss()
                }
            }
            dialog.title="Un-Match"
            dialog.desc="Are you sure you want to un-match this user?"
            dialog.user=user
            dialog.show(parentFragmentManager,"")
        }
        binding.tvReport.setOnClickListener {
            val dialog=ReportBottomSheet(user)
            dialog.show(parentFragmentManager,"")
            dismiss()
        }
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }
}