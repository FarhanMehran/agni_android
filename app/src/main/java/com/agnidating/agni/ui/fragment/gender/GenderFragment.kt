package com.agnidating.agni.ui.fragment.gender

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.ActivityGenderBinding
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.activities.completeProfile.CompleteProfileViewModel
import com.agnidating.agni.utils.checkNetwork
import dagger.hilt.android.AndroidEntryPoint

/*
* Created by AJAY ASIJA
* */

@AndroidEntryPoint
class GenderFragment : ScopedFragment(), View.OnClickListener {
    private lateinit var binding: ActivityGenderBinding
    private var gender="M"
    private val viewModel: CompleteProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=ActivityGenderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBack(binding.ivBack)
        binding.onclick=this
        bindObserver()
    }

    /*
    * bind all observer here
    * */
    private fun bindObserver() {
        viewModel.updateLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> {
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    viewModel.updateLiveData.postValue(null)
                    findNavController().navigate(R.id.action_gender_to_showMe)
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }
    }


    /*
    * on click listeners
    * */
    override fun onClick(p0: View?) {
        when(p0){
            binding.btNext->{
                if (checkNetwork()){
                    viewModel.setGender(gender)
                }
            }
            binding.clMale->{
                gender="M"
                binding.clFemale.isChecked=false
                binding.tvFemale.isEnabled=false
                binding.clMale.isChecked=true
                binding.tvMale.isEnabled=true
            }
            binding.clFemale->{
                gender="F"
                binding.clFemale.isChecked=true
                binding.tvFemale.isEnabled=true
                binding.clMale.isChecked=false
                binding.tvMale.isEnabled=false
            }
        }
    }
}