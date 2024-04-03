package com.agnidating.agni.ui.fragment.showMe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.ActivityShowMeBinding
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.activities.completeProfile.CompleteProfileViewModel
import com.agnidating.agni.utils.checkNetwork
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/*
* Created by AJAY ASIJA
* */

@AndroidEntryPoint
class ShowMeFragment : ScopedFragment(), View.OnClickListener {
    private lateinit var binding: ActivityShowMeBinding
    private var gender="M"
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private val viewModel: CompleteProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=ActivityShowMeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBack(binding.ivBack)
        binding.onclick=this
        bindObserver()
    }

    /*
    * bind all observers here
    */
    private fun bindObserver() {
        viewModel.interestLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> {
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    viewModel.updateLiveData.postValue(null)
                    findNavController().navigate(R.id.action_showMe_to_nav_education)
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }
    }



    //================================== onClick =============================================
    override fun onClick(p0: View?) {
        when(p0){
            binding.btNext->{
                if (checkNetwork()){
                    viewModel.setInterest(gender)
                }
            }
            binding.clMale->{
                gender="M"
                binding.clFemale.isChecked=false
                binding.tvFemale.isEnabled=false
                binding.clBoth.isChecked=false
                binding.tvBoth.isEnabled=false
                binding.clMale.isChecked=true
                binding.tvMale.isEnabled=true
            }
            binding.clFemale->{
                gender="F"
                binding.clFemale.isChecked=true
                binding.tvFemale.isEnabled=true
                binding.clMale.isChecked=false
                binding.tvMale.isEnabled=false
                binding.clBoth.isChecked=false
                binding.tvBoth.isEnabled=false
            }
            binding.clBoth->{
                gender="B"
                binding.clFemale.isChecked=false
                binding.tvFemale.isEnabled=false
                binding.clMale.isChecked=false
                binding.tvMale.isEnabled=false
                binding.clBoth.isChecked=true
                binding.tvBoth.isEnabled=true
            }
        }
    }
}