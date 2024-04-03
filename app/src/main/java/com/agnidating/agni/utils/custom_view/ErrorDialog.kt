package com.agnidating.agni.utils.custom_view

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.agnidating.agni.R
import com.agnidating.agni.databinding.ErrorDialogBinding
import com.agnidating.agni.databinding.SuccessDialogBinding
import com.agnidating.agni.utils.toPx
import com.agnidating.agni.utils.visible
import kotlinx.coroutines.launch

/**
 * Create by AJAY ASIJA on 04/08/2022
 */
class ErrorDialog(private val msg:String?, private val onDismiss:()->Unit) : DialogFragment(){

    private lateinit var binding: ErrorDialogBinding
    var visible=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= ErrorDialogBinding.inflate(layoutInflater)
        binding.tvMsg.text=msg
        listeners()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (visible){
            binding.tvCancel.visible()
        }
    }

    private fun listeners() {
        binding.btnContinue.setOnClickListener {
            dismiss()
            onDismiss.invoke()
        }
        binding.tvCancel.setOnClickListener {
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