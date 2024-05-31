package com.agnidating.agni.ui.fragment.message

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.FragmentMessageBinding
import com.agnidating.agni.model.recent.Data
import com.agnidating.agni.ui.activities.dashboard.DashboardActivity
import com.agnidating.agni.ui.adapters.MessageAdapter
import com.agnidating.agni.ui.fragment.chat.ChatActivity
import com.agnidating.agni.utils.CommonKeys
import com.agnidating.agni.utils.gone
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import com.agnidating.agni.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.truncate

@AndroidEntryPoint
class MessageFragment:ScopedFragment() {
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var binding: FragmentMessageBinding
    private val viewModel:MessageViewModel by viewModels()
    @Inject lateinit var sharedPrefs: SharedPrefs
    private val list=ArrayList<Data>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentMessageBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefs.save(CommonKeys.HAVE_NEW_MESSAGES,false)
        (requireActivity() as DashboardActivity).showHideBadge(false)
        (requireActivity() as ScopedActivity).showProgress()
        messageAdapter=MessageAdapter(list,sharedPrefs=sharedPrefs){
            val userId= if (sharedPrefs.getString(CommonKeys.USER_ID)==it.senderId) it.receiverId else it.senderId
            val intent=Intent(requireActivity(),ChatActivity::class.java)
            intent.putExtra(CommonKeys.RECEIVER_ID,userId.toInt())
            intent.putExtra(CommonKeys.SUSPENDED,it.receiverSuspendedStatus=="1")
            startActivity(intent)
            /*val action= MessageFragmentDirections.actionMessageFragmentToChatFragment(userId.toInt())
            findNavController().navigate(action)*/
        }
        bindObserver()
        binding.rvMessageList.adapter=messageAdapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.connectSocket()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun bindObserver() {


        Log.d("bindObserver","call")

        viewModel.recentChat.observe(viewLifecycleOwner){
            (requireActivity() as ScopedActivity).hideProgress()
            if(it.data.isNullOrEmpty().not()){
                list.clear()
                list.addAll(it.data)
                binding.rvMessageList.visible()
                binding.rlNoMessage.gone()
                messageAdapter.notifyDataSetChanged()

                Log.d("recent_chat_observer", true.toString())
            }
            else{
                Log.d("recent_chat_observer",false.toString())
                binding.rvMessageList.gone()
                binding.rlNoMessage.visible()
            }
        }
    }
}