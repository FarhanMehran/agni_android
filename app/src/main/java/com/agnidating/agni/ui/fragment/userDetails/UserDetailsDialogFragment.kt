package com.agnidating.agni.ui.fragment.userDetails

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.databinding.FragmentUserDetailsBinding
import com.agnidating.agni.model.user_details.UserDetails
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.adapters.ViewPagerAdapter
import com.agnidating.agni.ui.fragment.chat.ChatActivity
import com.agnidating.agni.ui.fragment.home.HomeViewModel
import com.agnidating.agni.ui.fragment.optionBottomSheet.OptionBottomSheet
import com.agnidating.agni.utils.CommonKeys
import com.agnidating.agni.utils.gone
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import com.agnidating.agni.utils.showToast
import com.agnidating.agni.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by AJAY ASIJA on 04/22/2022
 */

@AndroidEntryPoint
class UserDetailsDialogFragment() : DialogFragment() {
    var callBack: ((Boolean) -> Unit)? = null
    private lateinit var viewPagerAdapter: ViewPagerAdapter
     var arrayList=ArrayList<Int>()
    var hideMessage: Boolean=false
    var matchStatus:Boolean=false
    private lateinit var binding: FragmentUserDetailsBinding
    private var matched=false
    var id=""
    private val viewModel: HomeViewModel by viewModels()
    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentUserDetailsBinding.inflate(layoutInflater)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (id.isEmpty()){
            viewModel.getUserDetails(arguments?.getString(CommonKeys.USER_ID).toString())
        }else{
            viewModel.getUserDetails(id)
        }
        if (hideMessage){
            binding.clType.gone()
        }else{
           binding.etSendFlower.isVisible=matchStatus
            binding.clType.isVisible=matchStatus.not()
        }
        bindObserver()
        clickView()
    }


    private fun bindObserver() {
        viewModel.userInfo.observe(viewLifecycleOwner){
            when (it) {
                is ResultWrapper.Loading -> {
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showData(it)
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    it.error?.message?.toast(requireContext())
                }
            }

        }
        viewModel.sentLiveData.observe(viewLifecycleOwner) {
            if(it!=null){
                requireActivity().showToast(it)
            }
        }
    }

    /**
     * show user details
     */
    private fun showData(it: ResultWrapper.Success<UserDetails>) {
        matched=it.data.matchStatus==1
        if(it.data.data.profileImg!=null){
            viewPagerAdapter= ViewPagerAdapter(it.data.data.profileImg!!)
            binding.vpUser.adapter=viewPagerAdapter
            setupIndicator()
        }

        binding.user=it.data.data
    }


    /**
     * setup indicator for viewPager
     */
    private fun setupIndicator() {
        binding.indicatorView.apply {
            setSliderWidth(resources.getDimension(com.intuit.sdp.R.dimen._100sdp))
            setSliderHeight(resources.getDimension(com.intuit.sdp.R.dimen._5sdp))
            setupWithViewPager(binding.vpUser)
        }
    }

    /**
     * Manage all click events
     */
    private fun clickView() {
        binding.btClose.setOnClickListener {
            if (id.isEmpty()){
                findNavController().popBackStack()
            }else{
                dismiss()
            }
           // callBack?.invoke(true)
        }

        binding.etSendFlower.setOnClickListener {
            val userId= id.toInt()
            val intent= Intent(requireActivity(), ChatActivity::class.java)
            intent.putExtra(CommonKeys.RECEIVER_ID,userId)
            startActivity(intent)
        }
        binding.ivMenu.setOnClickListener {
            OptionBottomSheet(binding.user!!).apply {
                showUnMatch=matched
                callback={
                    if (it=="block"){
                        binding.clType.visibility=View.INVISIBLE
                        this@UserDetailsDialogFragment.callBack?.invoke(true)
                        binding.btClose.performClick()
                    }
                }
            }.show(parentFragmentManager, "")
        }
        binding.ivHeart.setOnClickListener {
            if (binding.etTypeSomeThing.text.isNullOrEmpty().not()){
                val map=HashMap<String,String>()
                map["targetId"]= binding.user?.id+""
                map["msg"]= binding.etTypeSomeThing.text.toString()
                map["status"]="1"
                viewModel.sendRequest(map)
                /*viewModel.connectSocket(
                    binding.etTypeSomeThing.text.toString(), binding.user!!.id.toInt(), sharedPrefs.getString(
                        CommonKeys.USER_ID
                    )!!.toInt()
                )
                binding.etTypeSomeThing.setText("")*/
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

}