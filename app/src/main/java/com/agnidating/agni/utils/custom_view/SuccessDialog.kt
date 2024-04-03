package com.agnidating.agni.utils.custom_view

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.agnidating.agni.R
import com.agnidating.agni.databinding.SuccessDialogBinding
import com.agnidating.agni.utils.toPx

/**
 * Create by AJAY ASIJA on 04/08/2022
 */
class SuccessDialog(private val msg:String?,private val onDismiss:()->Unit) : DialogFragment(){

    private lateinit var binding: SuccessDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= SuccessDialogBinding.inflate(layoutInflater)
        binding.tvMsg.text=msg
        listeners()
        return binding.root
    }

    private fun listeners() {
        binding.btnContinue.setOnClickListener {
            dismiss()
            onDismiss.invoke()
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