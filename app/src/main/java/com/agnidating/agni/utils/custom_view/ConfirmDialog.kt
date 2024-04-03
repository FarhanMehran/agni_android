package com.agnidating.agni.utils.custom_view

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.agnidating.agni.R
import com.agnidating.agni.databinding.DialogBlockBinding
import com.agnidating.agni.model.home.User
import com.agnidating.agni.utils.toPx

/**
 * Create by AJAY ASIJA on 04/08/2022
 */
class ConfirmDialog(private val onDismiss:(Boolean)->Unit) : DialogFragment(){

    private lateinit var binding: DialogBlockBinding
    var title=""
    var desc=""
    var user:User?=null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= DialogBlockBinding.inflate(layoutInflater)
        binding.tvDescrip.text=desc
        binding.tvTittle.text=title
        listeners()
        return binding.root
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