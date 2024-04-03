package com.agnidating.agni.utils.custom_view

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.agnidating.agni.R
import com.agnidating.agni.databinding.DialogBlockBinding
import com.agnidating.agni.databinding.DialogDeleteBinding
import com.agnidating.agni.databinding.DialogUnblockBinding
import com.agnidating.agni.model.blockUser.BlockedUser
import com.agnidating.agni.model.home.User
import com.agnidating.agni.utils.CommonKeys
import com.agnidating.agni.utils.load
import com.agnidating.agni.utils.toPx

/**
 * Create by AJAY ASIJA on 04/08/2022
 */
class UnblockDialog(private val onDismiss:(Boolean)->Unit) : DialogFragment(){

    private lateinit var binding: DialogUnblockBinding

    var user: Any? =null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= DialogUnblockBinding.inflate(layoutInflater)
        listeners()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (user is User){
            binding.tvName.text= (user as User).name
            binding.civProfile.load((user as User).profileImg?.get(0)?.profile.toString(),CommonKeys.IMAGE_BASE_URL)
        }else{
            binding.tvName.text= (user as BlockedUser).name
            binding.civProfile.load( (user as BlockedUser).profilePicture,CommonKeys.IMAGE_BASE_URL)
        }
    }

    private fun listeners() {
        binding.btnYes.setOnClickListener {
            onDismiss.invoke(true)
            dismiss()
        }
        binding.btnNo.setOnClickListener {
            onDismiss.invoke(false)
            dismiss()
        }
    }
    override fun onStart() {
        super.onStart()
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels - 50f.toPx
        dialog!!.window!!.setLayout(width.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog!!.window!!.setBackgroundDrawableResource(R.color.white_opacity_2)
    }
}