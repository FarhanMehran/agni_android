package com.agnidating.agni.utils.custom_view

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.agnidating.agni.R
import com.agnidating.agni.databinding.FolwerAlertBinding
import com.agnidating.agni.model.home.User
import com.agnidating.agni.utils.toPx
import com.agnidating.agni.utils.visible

/**
 * Create by AJAY ASIJA on 04/08/2022
 */
class FlowerDialog() : DialogFragment(){

    var type: Int=0
    private lateinit var binding: FolwerAlertBinding
    var title=""
    var desc=""
    var user:User?=null
    var onDismiss: ((Int,String) -> Unit)? =null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FolwerAlertBinding.inflate(layoutInflater)
        binding.tvDesc.text=desc
        binding.tvTittle.text=title
        if (type==0){
            binding.btBuyFlower.text=getString(R.string.buy_now)
        }else{
            binding.btBuyFlower.text=getString(R.string.send)
            binding.etMsg.visible()
        }
        listeners()
        return binding.root
    }

    private fun listeners() {
        binding.btBuyFlower.setOnClickListener {
            val flowers=if (binding.rbSingle.isChecked) 1 else 5
            onDismiss?.invoke(flowers,binding.etMsg.text.toString())
            dismiss()
        }
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
        binding.rgFlower.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rbSingle->binding.ivImg.setImageResource(R.drawable.ic_rose)
                R.id.rbBunch->binding.ivImg.setImageResource(R.drawable.flower)
            }
        }
    }
    override fun onStart() {
        super.onStart()
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels - 16f.toPx
        dialog!!.window!!.setLayout(width.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog!!.window!!.setBackgroundDrawableResource(R.color.white_opacity_2)
    }
}