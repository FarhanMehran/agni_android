package com.agnidating.agni.ui.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.agnidating.agni.base.ScopedFragment
import com.agnidating.agni.databinding.FragmentOurPoliciesBinding
import com.agnidating.agni.utils.openChromeTab
import dagger.hilt.android.AndroidEntryPoint

/**
 *
 * Created by AJAY ASIJA
 *
 */
@AndroidEntryPoint
class OurPrivacyPolicyFragment : ScopedFragment() {
    private lateinit var binding: FragmentOurPoliciesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentOurPoliciesBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvPrivacyPolicy.setOnClickListener {
            requireActivity().openChromeTab("https://agnidating.net/privacyPolicy")
        }
        binding.tvAboutUs.setOnClickListener {
            requireActivity().openChromeTab("https://agnidating.net/about")
        }
        binding.tvTermPolicy.setOnClickListener {
            requireActivity().openChromeTab("https://agnidating.net/termCondition")
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}