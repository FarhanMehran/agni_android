package com.agnidating.agni.ui.fragment.youGotMatch

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.agnidating.agni.R
import com.agnidating.agni.databinding.YouGotMatchBinding
import com.agnidating.agni.model.notification.Data
import com.agnidating.agni.ui.fragment.chat.ChatActivity
import com.agnidating.agni.utils.CommonKeys
import com.agnidating.agni.utils.load
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class YouGotMatch:DialogFragment() {
    var data: Data?=null
    var callBack: ((Boolean) -> Unit)? = null

    @Inject lateinit var sharedPrefs:SharedPrefs
    private lateinit var binding: YouGotMatchBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= YouGotMatchBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setupListerners()
    }

    private fun initView() {
     //   binding.tvGradient.text=data?.notificationMsg
        binding.ivUser1.load(data?.senderImage,CommonKeys.IMAGE_BASE_URL)
        binding.ivUser2.load(data?.receiverImage,CommonKeys.IMAGE_BASE_URL)
    }

    /**
     * set up all listeners
     */
    private fun setupListerners() {
        binding.consClose.setOnClickListener {
            callBack?.invoke(true)
        }
        binding.consFour.setOnClickListener {
            val userId= if (sharedPrefs.getString(CommonKeys.USER_ID)==data?.senderId) data?.receiverId else data?.senderId
            val intent= Intent(requireActivity(), ChatActivity::class.java)
            intent.putExtra(CommonKeys.FROM_NOTIFICATION,true)
            intent.putExtra(CommonKeys.RECEIVER_ID,userId?.toInt())
            startActivity(intent)
           // findNavController().navigate(R.id.chat_fragment)
        }
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }
}