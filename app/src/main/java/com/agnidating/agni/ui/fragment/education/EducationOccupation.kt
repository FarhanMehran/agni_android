package com.agnidating.agni.ui.fragment.education

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.FragmentEducationOccupationBinding
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.activities.completeProfile.CompleteProfileViewModel
import com.agnidating.agni.utils.CommonKeys
import com.agnidating.agni.utils.gone
import dagger.hilt.android.AndroidEntryPoint

/**
 * Create by AJAY ASIJA on 05/27/2022
 */
@AndroidEntryPoint
class EducationOccupation : ScopedFragment() {

    private lateinit var binding: FragmentEducationOccupationBinding
    private val viewModel: CompleteProfileViewModel by viewModels()
    var update = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEducationOccupationBinding.inflate(layoutInflater)
        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        update = arguments?.getBoolean(CommonKeys.FOR_UPDATE, false) == true
        if (update) {
            binding.etOccupation.setText(arguments?.getString(CommonKeys.OCCUPATION).toString())
            binding.tvCounts.text = "${String.format("%02d", binding.etOccupation.text?.length)}/50"
            binding.btNext.text = getString(R.string.save)
            selectEducation()
        }
        if (update.not()) {
            handleBack(binding.ivBack)
        } else {
            binding.ivBack.setOnClickListener {
                binding.btNext.performClick()
            }
        }
        binding.btNext.setOnClickListener {
            if (binding.etOccupation.text?.trim().isNullOrEmpty()) {
                showToast("Occupation is mandatory")
            } else {
                val map = HashMap<String, String>()
                map["occupation"] = binding.etOccupation.text.toString()
                map["education"] =
                    binding.root.findViewById<RadioButton>(binding.rgEducation.checkedRadioButtonId).text.toString()
                map["status"] = if (update) "0" else "1"
                viewModel.setEducationOccupation(map)
            }
        }
        bindObserver()
        textCountChangeListener()
    }

    private fun selectEducation() {
        binding.rgEducation.check(
            when (arguments?.getString(CommonKeys.EDUCATION).toString()) {
                getString(R.string.graduation) -> R.id.rbGraduation
                getString(R.string.high_school)  -> R.id.rbHighSchool
                getString(R.string.post_grad)  -> R.id.rbPg
                else -> R.id.rbGraduation
            }
        )
    }

    private fun textCountChangeListener() {
        binding.etOccupation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val count = char?.length
                binding.tvCounts.text = "${String.format("%02d", count)}/50"
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    /**
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
                    if (update) {
                        findNavController().popBackStack()
                    } else {
                        findNavController().navigate(R.id.action_nav_education_to_nav_religion)
                    }
                }
                is ResultWrapper.Error -> {
                    (requireActivity() as ScopedActivity).hideProgress()
                    showToast(it.error?.message.toString())
                }
            }
        }
    }

}