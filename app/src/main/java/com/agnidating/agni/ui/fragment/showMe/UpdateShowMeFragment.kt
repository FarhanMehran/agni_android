package com.agnidating.agni.ui.fragment.showMe

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.ActivityShowMeBinding
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.fragment.settings.SettingsFragmentViewModel
import com.agnidating.agni.utils.checkNetwork
import com.agnidating.agni.utils.gone
import com.agnidating.agni.utils.successDialog
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by AJAY ASIJA
 */

@AndroidEntryPoint
class UpdateShowMeFragment : ScopedFragment(), View.OnClickListener {
    private lateinit var binding: ActivityShowMeBinding
    private var gender = "M"
    private val viewModel: SettingsFragmentViewModel by viewModels()
    private val args: UpdateShowMeFragmentArgs by navArgs()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityShowMeBinding.inflate(layoutInflater, container, false)
        binding.onclick = this
        binding.btNext.gone()
        initView()
        bindObserver()
        binding.ivBack.setOnClickListener(this)
        return binding.root
    }


    /**
     *Select gender selected if user has already updated interested gender
     *
     */
    private fun initView() {
        if (args.interestedGender.isNotEmpty()) {
            when (args.interestedGender) {
                "M" -> {
                    selectMale()
                }
                "F" -> {
                    selectFemale()
                }
                else -> {
                    selectBoth()
                }
            }
        }
    }

    /**
     * bind all observers here
     */
    private fun bindObserver() {
        viewModel.baseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> {
                    (requireActivity() as ScopedActivity).showProgress()
                }
                is ResultWrapper.Success -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    findNavController().popBackStack()
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }
    }


    /**
     * On click events [onClick]
     */
    override fun onClick(p0: View?) {
        when (p0) {
            binding.ivBack -> {
                binding.btNext.performClick()
            }
            binding.btNext -> {
                if (checkNetwork()) {
                    viewModel.updateInterest(gender)
                }
            }
            binding.clMale -> {
                selectMale()
            }
            binding.clFemale -> {
                selectFemale()
            }
            binding.clBoth -> {
                selectBoth()
            }
        }
    }

    /**
     * make male button selected
     */
    private fun selectMale() {
        gender="M"
        binding.clFemale.isChecked=false
        binding.tvFemale.isEnabled=false
        binding.clBoth.isChecked=false
        binding.tvBoth.isEnabled=false
        binding.clMale.isChecked=true
        binding.tvMale.isEnabled=true
    }

    /**
     * Make female button selected
     */
    private fun selectFemale() {
        gender="F"
        binding.clFemale.isChecked=true
        binding.tvFemale.isEnabled=true
        binding.clMale.isChecked=false
        binding.tvMale.isEnabled=false
        binding.clBoth.isChecked=false
        binding.tvBoth.isEnabled=false
    }
    /**
     * Make both button selected
     */
    private fun selectBoth() {
        gender="B"
        binding.clFemale.isChecked=false
        binding.tvFemale.isEnabled=false
        binding.clMale.isChecked=false
        binding.tvMale.isEnabled=false
        binding.clBoth.isChecked=true
        binding.tvBoth.isEnabled=true
    }
}