package com.agnidating.agni.ui.fragment.yourName


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.ActivityYourNameBinding
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.activities.completeProfile.CompleteProfileViewModel
import com.agnidating.agni.utils.checkNetwork
import com.agnidating.agni.utils.isValidPersonName
import dagger.hilt.android.AndroidEntryPoint


/*
* Created by AJAY ASIJA
* */

@AndroidEntryPoint
class YourNameFragment : ScopedFragment(), View.OnClickListener {
    private lateinit var binding: ActivityYourNameBinding
    private val viewModel: CompleteProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityYourNameBinding.inflate(inflater, container, false)
        initViews()
        return binding.root
    }

    /*
    * initialize views and listeners
    * */
    private fun initViews() {
        binding.onclick = this
        handleBack(binding.ivBack)
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
                    findNavController().navigate(R.id.action_navYourName_to_navLocation)
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.btGoNext -> {
                if (isValid()) {
                    viewModel.setName(binding.etName.text.toString().trim())
                }
            }
        }
    }

    private fun isValid(): Boolean {
        var valid = checkNetwork()
        if (binding.etName.text.toString().isEmpty()) {
            valid = false
            showToast("Please Enter Name")
        }
        else if (binding.etName.text.toString().trim().isValidPersonName().not()) {
            valid = false
            showToast("Please Enter valid person name")
        }
        return valid
    }
}